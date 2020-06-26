package dev.forkhandles.bunting

import java.util.UUID
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField

abstract class Bunting(internal val args: Array<String>) {
    fun requiredFlag(description: String? = null) = BuntingFlag({ it }, description, null)
    fun defaultedFlag(default: String?, description: String? = null) = BuntingFlag({ it }, description, default)

    internal fun description() = this::class.members
        .filterIsInstance<KProperty<*>>()
        .mapNotNull { p ->
            listOf(p.javaField!!.apply { trySetAccessible() }[this]!!)
                .filterIsInstance<BuntingFlag<*>>()
                .firstOrNull()
                ?.let { p.name to (it.description ?: "") }
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

fun BuntingFlag<String>.int() = map(String::toInt)
fun BuntingFlag<String>.float() = map(String::toFloat)
fun BuntingFlag<String>.long() = map(String::toLong)
fun BuntingFlag<String>.uuid() = map(UUID::fromString)
fun BuntingFlag<String>.char() = map(String::first)
fun BuntingFlag<String>.boolean() = map(String::toBoolean)
inline fun <reified T : Enum<T>> BuntingFlag<String>.enum() =
    copy(description = (description ?: "") + ". Option choice: " + enumValues<T>().toList()).map { enumValueOf<T>(it) }
