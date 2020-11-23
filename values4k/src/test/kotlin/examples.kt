import dev.forkhandles.result4k.recover
import dev.forkhandles.result4k.resultFrom
import dev.forkhandles.values.IntValueFactory
import dev.forkhandles.values.Maskers.hidden
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.Value
import dev.forkhandles.values.minValue
import dev.forkhandles.values.regex

class Money private constructor(value: Int) : Value<Int>(value) {
    companion object : IntValueFactory<Money>(::Money, 1.minValue)
}

class SortCode private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<SortCode>(::SortCode, "\\d{6}".regex)
}

class AccountNumber private constructor(value: String) : Value<String>(value, hidden()) {
    companion object : StringValueFactory<AccountNumber>(::AccountNumber, "\\d{8}".regex)
}

fun main() {
    printOrError { SortCode.of("123qwe") }
    printOrError { AccountNumber.of("12345678") }
    printOrError { SortCode.parse("123456") }
    printOrError { SortCode.of("123qwe") }
    printOrError { AccountNumber.of("12345678") }
}

private fun printOrError(fn: () -> Value<String>) = println(resultFrom(fn).recover { it.message })
