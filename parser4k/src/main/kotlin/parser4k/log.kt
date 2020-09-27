package parser4k

import java.util.LinkedList

fun <T> Parser<T>.with(parserId: String, log: ParsingLog) = object : Parser<T> {
    override fun parse(input: Input): Output<T>? {
        log.before(parserId, input)
        val output = this@with.parse(input)
        log.after(parserId, output)
        return output
    }
}

class ParsingLog(private val onEvent: (ParsingEvent) -> Unit = { println(it.toDebugString()) }) {
    private val idStack = LinkedList<String>()
    private val inputStack = LinkedList<Input>()

    internal fun before(parserId: String, input: Input) {
        idStack.push(parserId)
        inputStack.push(input)
        onEvent(BeforeParsing(input, stackTrace()))
    }

    internal fun <T> after(parserId: String, output: Output<T>?) {
        onEvent(AfterParsing(inputStack.peek(), output, stackTrace()))
        inputStack.pop()
        idStack.pop().let { id ->
            require(id == parserId) { "Expected id '$parserId' but was '$id'" }
        }
    }

    private fun stackTrace(): List<StackFrame> =
        idStack.zip(inputStack)
            .map { (id, input) -> StackFrame(id, input.offset) }
            .asReversed()
}

fun List<ParsingEvent>.print() = forEach { println(it.toDebugString()) }

sealed class ParsingEvent
data class BeforeParsing(val input: Input, val stackTrace: List<StackFrame>) : ParsingEvent()
data class AfterParsing<T>(val input: Input, val output: Output<T>?, val stackTrace: List<StackFrame>) : ParsingEvent()

data class StackFrame(val parserId: String, val offset: Int)

fun ParsingEvent.toDebugString() =
    when (this) {
        is BeforeParsing -> "${input.string()} ${stackTrace.string()}"
        is AfterParsing<*> -> "${input.string()} ${stackTrace.string()} -- ${if (output == null) "no match" else input.diff(output.nextInput)}"
    }

private fun Input.diff(that: Input) = value.substring(this.offset, that.offset)

private fun List<StackFrame>.string() = joinToString(" ") { it.parserId + ":" + it.offset }

private fun Input.string() = "\"$value\""
