package dev.forkhandles.bunting

import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField

open class Bunting(internal val args: Array<String>, internal val baseCommand: String = System.getProperty("sun.java.command")) {
    fun switch(description: String = "") = Switch(description)
    fun option(description: String = "") = Option({ it }, description, null)
    fun <T : Bunting> command(fn: (Array<String>) -> T, description: String = "") = Command(args.drop(1), description, fn)

    internal fun description(indent: Int = 0): String {
        val commands = members { p, c: Command<*> -> p.name to c.getValue(Bunting(arrayOf(p.name), "$baseCommand ${p.name}"), p).description() }
        val switches = members { p, s: Switch -> p.name to s.description }
        val options = members { p, o: Option<*> -> p.name to "${o.description} (${p.typeDescription()})" }

        return (switches + options).sortedBy { it.first }.describeOptions()
    }

    private fun List<Pair<String, String>>.describeOptions() = """Options:
${joinToString("\n") { "\t-${it.first.take(1)}, --${it.first}\t\t${it.second}" }}
    -h, --help          Show this message and exit"""
}

fun <T : Bunting> T?.use(out: (String) -> Unit = ::println, fn: T.() -> Unit) =
    this?.apply {
        try {
            if (args.contains("--help") || args.contains("-h")) throw Help(description())
            fn(this)
        } catch (e: BuntingException) {
            out("Usage: $baseCommand [OPTIONS]\n" + e.localizedMessage)
        }
    }

private inline fun <reified F : BuntingFlag<*>> Bunting.members(fn: (KProperty<*>, F) -> Pair<String, String>): List<Pair<String, String>> =
    this::class.members.filterIsInstance<KProperty<F>>().mapNotNull { p ->
        (p.javaField!!.apply { trySetAccessible() }[this@members] as? F)
            ?.let {
                fn(p, it)
            }
    }
