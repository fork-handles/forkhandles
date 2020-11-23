import dev.forkhandles.result4k.recover
import dev.forkhandles.result4k.resultFrom
import dev.forkhandles.values.Maskers.hidden
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.Value
import dev.forkhandles.values.ValueFactory
import dev.forkhandles.values.minValue
import dev.forkhandles.values.regex

class Money private constructor(value: Int) : Value<Int>(value) {
    companion object : ValueFactory<Money, Int>(::Money, 1.minValue)
}

class SortCode private constructor(value: String) : StringValue(value) {
    companion object : ValueFactory<SortCode, String>(::SortCode, "\\d{6}".regex)
}

class AccountNumber private constructor(value: String) : Value<String>(value, hidden()) {
    companion object : ValueFactory<AccountNumber, String>(::AccountNumber, "\\d{8}".regex)
}

fun main() {
    printOrError { SortCode.of("123456") }
    printOrError { SortCode.of("123qwe") }
    printOrError { AccountNumber.of("12345678") }
}

private fun printOrError(fn: () -> Value<String>) = println(resultFrom(fn).recover { it.message })
