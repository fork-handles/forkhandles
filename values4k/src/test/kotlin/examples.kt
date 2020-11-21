import dev.forkhandles.result4k.recover
import dev.forkhandles.result4k.resultFrom
import dev.forkhandles.values.Maskers.hidden
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.Value
import dev.forkhandles.values.minValue
import dev.forkhandles.values.regex

class Money(value: Int) : Value<Int>(value, 1.minValue)

val pattern = "\\d{6}".regex // cache
class SortCode(value: String) : StringValue(value, pattern)

class AccountNumber(value: String) : Value<String>(value, "\\d{8}".regex, hidden())

fun main() {
    printOrError { SortCode("123456") }
    printOrError { SortCode("123qwe") }
    printOrError { AccountNumber("12345678") }
}

private fun printOrError(fn: () -> Value<String>) = println(resultFrom(fn).recover { it.message })
