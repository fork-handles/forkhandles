@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

import dev.forkhandles.result4k.recover
import dev.forkhandles.result4k.resultFrom
import dev.forkhandles.values.AbstractValue
import dev.forkhandles.values.IntValueFactory
import dev.forkhandles.values.Maskers.hidden
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.Value
import dev.forkhandles.values.minValue
import dev.forkhandles.values.regex

// standard value types can extend the base AbstractValue type....
class BottlesOfBeer private constructor(value: Int) : AbstractValue<Int>(value) {
    companion object : IntValueFactory<BottlesOfBeer>(::BottlesOfBeer)
}

// ...or use one of th built in typealiases. Validation rules can also be passed in...
class SortCode private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<SortCode>(::SortCode, "\\d{6}".regex)
}

// ...you can also hide the value from being accidentally exposed through toString() calls...
class AccountNumber private constructor(value: String) : AbstractValue<String>(value, hidden()) {
    companion object : StringValueFactory<AccountNumber>(::AccountNumber, "\\d{8}".regex)
}

// ...value classes can also be used by extending the base Value interface.
// ... private constructors are available in Kotlin 1.5.0
@JvmInline
value class Money private constructor(override val value: Int) : Value<Int> {
    companion object : IntValueFactory<Money>(::Money, 1.minValue)
}

fun main() {
    printOrError { BottlesOfBeer.of(99) } // constructs ok
    printOrError { Money.of(0) } // will blow up
    printOrError { Money.parse("not money") } // will blow up
    printOrError { Money.show(Money.of(123)) } // prints 123

    printOrError { SortCode.of("123qwe") } // will blow up
    printOrError { AccountNumber.of("12345678") } // masks value
    printOrError { SortCode.parse("123456") } // constructs ok
    printOrError { SortCode.of("123qwe") } // will blow up
    printOrError { AccountNumber.parse("1234567") } // will blow up
}

private fun printOrError(fn: () -> Any) =
    println(resultFrom(fn).recover { """${it.javaClass.simpleName}: ${it.message}""" })
