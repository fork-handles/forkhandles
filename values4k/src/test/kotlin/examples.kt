import dev.forkhandles.result4k.recover
import dev.forkhandles.result4k.resultFrom
import dev.forkhandles.values.LocalDateValue
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.Value
import dev.forkhandles.values.regex
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * A sort code is 6 digits long
 */
class SortCode(value: String) : StringValue(value, "\\d{6}".regex)

val format = DateTimeFormatter.BASIC_ISO_DATE

class MyDate(value: LocalDate) : LocalDateValue(value, { true }) {
    constructor(value: String) : this(try {
        LocalDate.parse(value, format)
    } catch (e: DateTimeParseException) {
        throw e
    })
}

fun main() {
    printOrError { SortCode("123456") }
    printOrError { SortCode("123qwe") }
}

private fun printOrError(fn: () -> Value<String>) = println(resultFrom(fn).recover { it.message })
