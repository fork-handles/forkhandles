package dev.forkhandles.bunting

import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField

open class Bunting(
    internal val args: Array<String>,
    private val description: String? = null,
    private val baseCommand: String = System.getProperty("sun.java.command"),
    internal val io: IO = ConsoleIO
) {
    fun switch(description: String = "") = Switch(description)
    fun option(description: String = "") = Optional({ it }, description, { it }, io)
    fun <T : Bunting> command(fn: BuntingConstructor<T>) = Command(fn)

    internal fun usage(): String = "$baseCommand [commands] [options]"

    internal fun description(indent: Int = 0) =
        listOfNotNull(description?.let { indent(indent) + it }, commandDescriptions(indent), optionDescriptions(indent)).joinToString("\n")

    private fun commandDescriptions(indent: Int): String? {
        val commandDescriptions = members { p, c: Command<*> ->
            val suffix = c.getValue(Bunting(arrayOf(p.name), description, "$baseCommand ${p.name}"), p)
                ?.description(indent + 2)
                ?.takeIf { it.isNotBlank() }?.let { "\n" + it } ?: ""
            p.name to (c.description ?: "") + suffix
        }

        return commandDescriptions
            .takeIf { it.isNotEmpty() }
            ?.let {
                indent(indent) + (if (indent == 0) "[commands]" else "[subcommands]") + ":\n" +
                    it.joinToString("\n") {
                        "${indent(indent)}  ${it.first}".indented(it.second)
                    }
            }
    }

    private fun optionDescriptions(indent: Int): String? {
        val switches = members { p, s: Switch -> p.name to (s.description ?: "") }
        val optional = members { p, o: Optional<*> -> p.name to p.description(o) }
        val required = members { p, o: Required<*> -> p.name to p.description(o) }
        val defaulted = members { p, o: Defaulted<*> -> p.name to p.description(o) }
        val prompted = members { p, o: Prompted<*> -> p.name to p.description(o) }

        val sortedOptions = (switches + optional + required + defaulted + prompted).sortedBy { it.first }
        val allOptions = if (indent > 0) sortedOptions else sortedOptions + listOf("help" to "Show this message and exit")

        return allOptions.takeIf { it.isNotEmpty() }?.describeOptions(indent)
    }
}

private fun KProperty<*>.description(o: BuntingFlag<*>) = "${o.description} (${typeDescription()})"

typealias BuntingConstructor<T> = (Array<String>) -> T

fun <T : Bunting> T?.use(fn: T.() -> Unit) {
    this?.apply {
        try {
            if (args.contains("--help") || args.contains("-h")) throw Help(description())
            fn(this)
        } catch (e: BuntingException) {
            io.write("Usage: ${usage()}\n" + e.localizedMessage)
        }
    }
}

private inline fun <reified F : BuntingFlag<*>> Bunting.members(fn: (KProperty<*>, F) -> Pair<String, String>): List<Pair<String, String>> =
    this::class.members.filterIsInstance<KProperty<F>>().mapNotNull { p ->
        (p.javaField!!.apply { trySetAccessible() }[this@members] as? F)
            ?.let {
                fn(p, it)
            }
    }

private fun indent(indent: Int) = "  ".repeat(indent)

private fun List<Pair<String, String>>.describeOptions(indent: Int) = indent(indent) + "[options]:" + "\n" +
    joinToString("\n") {
        (indent(indent + 1) + "-${it.first.take(1)}, --${it.first}").indented(it.second)
    }

private fun String.indented(second: String) = this + " ".repeat(maxOf(40 - length, 4)) + second
