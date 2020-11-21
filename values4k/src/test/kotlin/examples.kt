import dev.forkhandles.result4k.recover
import dev.forkhandles.result4k.resultFrom
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.Value
import dev.forkhandles.values.regex

/**
 * A sort code is 6 digits long
 */
class SortCode(value: String) : StringValue(value, "\\d{6}".regex)

fun main() {
    printOrError { SortCode("123456") }
    printOrError { SortCode("123qwe") }
}

private fun printOrError(fn: () -> Value<String>) = println(resultFrom(fn).recover { it.message })
