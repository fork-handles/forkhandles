package dev.forkhandles.values

import dev.forkhandles.values.Constraint.Companion.AssertFalse
import dev.forkhandles.values.Constraint.Companion.AssertTrue
import dev.forkhandles.values.Constraint.Companion.DecimalMax
import dev.forkhandles.values.Constraint.Companion.DecimalMin
import dev.forkhandles.values.Constraint.Companion.Digits
import dev.forkhandles.values.Constraint.Companion.Email
import dev.forkhandles.values.Constraint.Companion.Future
import dev.forkhandles.values.Constraint.Companion.FutureOrPresent
import dev.forkhandles.values.Constraint.Companion.Max
import dev.forkhandles.values.Constraint.Companion.Min
import dev.forkhandles.values.Constraint.Companion.Negative
import dev.forkhandles.values.Constraint.Companion.NegativeOrZero
import dev.forkhandles.values.Constraint.Companion.NotBlank
import dev.forkhandles.values.Constraint.Companion.NotEmpty
import dev.forkhandles.values.Constraint.Companion.NotNull
import dev.forkhandles.values.Constraint.Companion.Null
import dev.forkhandles.values.Constraint.Companion.Past
import dev.forkhandles.values.Constraint.Companion.PastOrPresent
import dev.forkhandles.values.Constraint.Companion.Pattern
import dev.forkhandles.values.Constraint.Companion.Positive
import dev.forkhandles.values.Constraint.Companion.PositiveOrZero
import dev.forkhandles.values.Constraint.Companion.Size
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant

@JvmInline
value class Constraint(override val value: String) : Value<String> {
    companion object {
        val NotNull = Constraint("NotNull")
        val Null = Constraint("Null")
        val NotBlank = Constraint("NotBlank")
        val NotEmpty = Constraint("NotEmpty")
        val Size = Constraint("Size")
        val Email = Constraint("Email")
        val Pattern = Constraint("Pattern")
        val Min = Constraint("Min")
        val Max = Constraint("Max")
        val DecimalMin = Constraint("DecimalMin")
        val DecimalMax = Constraint("DecimalMax")
        val Positive = Constraint("Positive")
        val PositiveOrZero = Constraint("PositiveOrZero")
        val Negative = Constraint("Negative")
        val NegativeOrZero = Constraint("NegativeOrZero")
        val Digits = Constraint("Digits")
        val AssertTrue = Constraint("AssertTrue")
        val AssertFalse = Constraint("AssertFalse")
        val Past = Constraint("Past")
        val PastOrPresent = Constraint("PastOrPresent")
        val Future = Constraint("Future")
        val FutureOrPresent = Constraint("FutureOrPresent")
        val Missing = Constraint("Missing")
        val Invalid = Constraint("Invalid")
        val Unsupported = Constraint("Unsupported")
        val Problems = Constraint("Problems")
    }
}

private val EMAIL = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")

fun <T> notNull(): Validator<T?> = { value ->
    if (value == null) ConstraintProblem(NotNull, Message("must not be null"), value.toString()) else null
}

fun <T> isNull(): Validator<T?> = { value ->
    if (value != null) ConstraintProblem(Null, Message("must be null"), value.toString()) else null
}

fun notBlank(): Validator<String?> = { value ->
    if (value.isNullOrBlank()) ConstraintProblem(NotBlank, Message("must not be blank"), value.toString()) else null
}

fun notEmpty(): Validator<String?> = { value ->
    if (value.isNullOrEmpty()) ConstraintProblem(NotEmpty, Message("must not be empty"), value.toString()) else null
}

fun size(min: Int = 0, max: Int = Int.MAX_VALUE): Validator<String?> = { value ->
    if (value == null || value.length !in min..max) {
        ConstraintProblem(Size, Message("size must be between $min and $max"), value.toString())
    } else {
        null
    }
}

fun email(): Validator<String?> = { value ->
    if (value == null || !EMAIL.matches(value)) {
        ConstraintProblem(Email, Message("must be a well-formed email address"), value.toString())
    } else {
        null
    }
}

fun pattern(regex: String): Validator<String?> = pattern(regex.toRegex())

fun pattern(regex: Regex): Validator<String?> = { value ->
    if (value == null || !regex.matches(value)) {
        ConstraintProblem(Pattern, Message("must match \"$regex\""), value.toString())
    } else {
        null
    }
}

fun <T : Comparable<T>> min(threshold: T): Validator<T?> = { value ->
    if (value == null || value < threshold) {
        ConstraintProblem(Min, Message("must be greater than or equal to $threshold"), value.toString())
    } else {
        null
    }
}

fun <T : Comparable<T>> max(threshold: T): Validator<T?> = { value ->
    if (value == null || value > threshold) {
        ConstraintProblem(Max, Message("must be less than or equal to $threshold"), value.toString())
    } else {
        null
    }
}

fun decimalMin(threshold: BigDecimal): Validator<BigDecimal?> = { value ->
    if (value == null || value < threshold) {
        ConstraintProblem(DecimalMin, Message("must be greater than or equal to $threshold"), value.toString())
    } else {
        null
    }
}

fun decimalMax(threshold: BigDecimal): Validator<BigDecimal?> = { value ->
    if (value == null || value > threshold) {
        ConstraintProblem(DecimalMax, Message("must be less than or equal to $threshold"), value.toString())
    } else {
        null
    }
}

fun <T : Number> positive(): Validator<T?> = { value ->
    if (value == null || BigDecimal(value.toString()) <= BigDecimal.ZERO) {
        ConstraintProblem(Positive, Message("must be greater than 0"), value.toString())
    } else {
        null
    }
}

fun <T : Number> positiveOrZero(): Validator<T?> = { value ->
    if (value == null || BigDecimal(value.toString()) < BigDecimal.ZERO) {
        ConstraintProblem(PositiveOrZero, Message("must be greater than or equal to 0"), value.toString())
    } else {
        null
    }
}

fun <T : Number> negative(): Validator<T?> = { value ->
    if (value == null || BigDecimal(value.toString()) >= BigDecimal.ZERO) {
        ConstraintProblem(Negative, Message("must be less than 0"), value.toString())
    } else {
        null
    }
}

fun <T : Number> negativeOrZero(): Validator<T?> = { value ->
    if (value == null || BigDecimal(value.toString()) > BigDecimal.ZERO) {
        ConstraintProblem(NegativeOrZero, Message("must be less than or equal to 0"), value.toString())
    } else {
        null
    }
}

fun digits(integer: Int, fraction: Int): Validator<BigDecimal?> = { value ->
    val message = Message("numeric value out of bounds (<$integer digits>.<$fraction digits> expected)")
    when {
        value == null -> ConstraintProblem(Digits, message, value.toString())
        else -> {
            val split = value.stripTrailingZeros().toPlainString().split('.')
            val integerDigits = split[0].removePrefix("-").length
            val fractionDigits = split.getOrElse(1) { "" }.length
            if (integerDigits > integer || fractionDigits > fraction) {
                ConstraintProblem(Digits, message, value.toString())
            } else {
                null
            }
        }
    }
}

fun isTrue(): Validator<Boolean?> = { value ->
    if (value != true) ConstraintProblem(AssertTrue, Message("must be true"), value.toString()) else null
}

fun isFalse(): Validator<Boolean?> = { value ->
    if (value != false) ConstraintProblem(AssertFalse, Message("must be false"), value.toString()) else null
}

fun past(clock: Clock = Clock.systemUTC()): Validator<Instant?> = { value ->
    if (value == null || !value.isBefore(Instant.now(clock))) {
        ConstraintProblem(Past, Message("must be a past date"), value.toString())
    } else {
        null
    }
}

fun pastOrPresent(clock: Clock = Clock.systemUTC()): Validator<Instant?> = { value ->
    if (value == null || value.isAfter(Instant.now(clock))) {
        ConstraintProblem(PastOrPresent, Message("must be a date in the past or in the present"), value.toString())
    } else {
        null
    }
}

fun future(clock: Clock = Clock.systemUTC()): Validator<Instant?> = { value ->
    if (value == null || !value.isAfter(Instant.now(clock))) {
        ConstraintProblem(Future, Message("must be a future date"), value.toString())
    } else {
        null
    }
}

fun futureOrPresent(clock: Clock = Clock.systemUTC()): Validator<Instant?> = { value ->
    if (value == null || value.isBefore(Instant.now(clock))) {
        ConstraintProblem(FutureOrPresent, Message("must be a date in the present or in the future"), value.toString())
    } else {
        null
    }
}

fun notEmptyCollection(): Validator<Collection<*>?> = { value ->
    if (value.isNullOrEmpty()) {
        ConstraintProblem(NotEmpty, Message("must not be empty"), value.toString())
    } else {
        null
    }
}

fun sizeCollection(min: Int = 0, max: Int = Int.MAX_VALUE): Validator<Collection<*>?> = { value ->
    if (value == null || value.size !in min..max) {
        ConstraintProblem(Size, Message("size must be between $min and $max"), value.toString())
    } else {
        null
    }
}
