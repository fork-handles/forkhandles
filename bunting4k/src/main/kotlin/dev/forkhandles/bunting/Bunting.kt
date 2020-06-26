package dev.forkhandles.bunting

import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField

abstract class Bunting(internal val args: Array<String>) {
    fun noValueFlag(description: String? = null) = NoValueFlag(description)
    fun requiredFlag(description: String? = null) = ValueFlag({ it }, description, null)
    fun defaultedFlag(default: String?, description: String? = null) = ValueFlag({ it }, description, default)

    internal fun description() = this::class.members
        .filterIsInstance<KProperty<*>>()
        .mapNotNull { p ->
            listOf(p.javaField!!.apply { trySetAccessible() }[this]!!)
                .filterIsInstance<BuntingFlag<*>>()
                .firstOrNull()
                ?.let { p.name to "${it.description ?: ""} (${p.typeDescription()})" }
        }
        .sortedBy { it.first }
        .describe()

    private fun List<Pair<String, String>>.describe() = """Options:
${joinToString("\n") { "\t-${it.first.take(1)}, --${it.first}\t\t${it.second}" }}
    -h, --help          Show this message and exit"""
}

fun <T : Bunting> T.use(out: (String) -> Unit = ::println, fn: T.() -> Unit) =
    try {
        if (args.contains("--help") || args.contains("-h")) throw Help(description())
        fn(this)
    } catch (e: BuntingException) {
        out("Usage: <name> [OPTIONS]\n" + e.localizedMessage)
    }
