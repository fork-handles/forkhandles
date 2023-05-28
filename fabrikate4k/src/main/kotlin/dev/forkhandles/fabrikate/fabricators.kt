package dev.forkhandles.fabrikate

import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URI
import java.net.URL
import java.time.*
import java.time.ZoneOffset.UTC
import java.util.*
import kotlin.random.asJavaRandom

fun interface Fabricator<T> : (Fabrikate) -> T

class BooleanFabricator : Fabricator<Boolean> {
    override fun invoke(fabrikate: Fabrikate): Boolean = fabrikate.config.random.nextBoolean()
}

class IntFabricator(
    private val from: Int? = null,
    private val until: Int? = null,
) : Fabricator<Int> {
    constructor(range: ClosedRange<Int>) : this(range.start, range.endInclusive)

    override fun invoke(fabrikate: Fabrikate): Int = when {
        until == null -> fabrikate.config.random.nextInt()
        from != null -> fabrikate.config.random.nextInt(from, until)
        else -> fabrikate.config.random.nextInt(until)
    }
}

class LongFabricator(
    private val from: Long? = null,
    private val until: Long? = null,
) : Fabricator<Long> {
    constructor(range: ClosedRange<Long>) : this(range.start, range.endInclusive)

    override fun invoke(fabrikate: Fabrikate): Long = when {
        until == null -> fabrikate.config.random.nextLong()
        from != null -> fabrikate.config.random.nextLong(from, until)
        else -> fabrikate.config.random.nextLong(until)
    }
}

class DoubleFabricator(
    private val from: Double? = null,
    private val until: Double? = null,
) : Fabricator<Double> {
    constructor(range: ClosedRange<Double>) : this(range.start, range.endInclusive)

    override fun invoke(fabrikate: Fabrikate): Double = when {
        until == null -> fabrikate.config.random.nextDouble()
        from != null -> fabrikate.config.random.nextDouble(from, until)
        else -> fabrikate.config.random.nextDouble(until)
    }
}

class FloatFabricator : Fabricator<Float> {
    override fun invoke(fabrikate: Fabrikate): Float = fabrikate.config.random.nextFloat()
}

class BigDecimalFabricator(
    private val size: Int = 10,
) : Fabricator<BigDecimal> {
    override fun invoke(fabrikate: Fabrikate) = BigInteger(size, fabrikate.config.random.asJavaRandom()).toBigDecimal()
}

class BigIntegerFabricator(
    private val size: Int = 10,
) : Fabricator<BigInteger> {
    override fun invoke(fabrikate: Fabrikate) = BigInteger(size, fabrikate.config.random.asJavaRandom())
}

class StringFabricator(
    private val length: IntRange = IntRange(1, 10),
    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9'),
) : Fabricator<String> {
    override fun invoke(fabrikate: Fabrikate) = (1..length.random(fabrikate.config.random))
        .map { charPool.random(fabrikate.config.random) }
        .joinToString("")
}

class CharFabricator(
    private val charPool: CharRange = ('A'..'z'),
) : Fabricator<Char> {
    override fun invoke(fabrikate: Fabrikate): Char = charPool.random(fabrikate.config.random)
}

class ByteFabricator : Fabricator<Byte> {
    override fun invoke(fabrikate: Fabrikate): Byte = fabrikate.config.random.nextBytes(1).first()
}

class BytesFabricator(
    private val size: Int = 10,
) : Fabricator<ByteArray> {
    override fun invoke(fabrikate: Fabrikate): ByteArray = fabrikate.config.random.nextBytes(size)
}

class InstantFabricator : Fabricator<Instant> {
    override fun invoke(fabrikate: Fabrikate): Instant =
        Instant.ofEpochSecond(fabrikate.config.random.nextLong(0, 1735689600))
}

class LocalDateFabricator : Fabricator<LocalDate> {
    override fun invoke(fabrikate: Fabrikate): LocalDate =
        LocalDate.ofInstant(InstantFabricator()(fabrikate), UTC)
}

class LocalTimeFabricator : Fabricator<LocalTime> {
    override fun invoke(fabrikate: Fabrikate): LocalTime =
        LocalTime.ofInstant(InstantFabricator()(fabrikate), UTC)
}

class LocalDateTimeFabricator : Fabricator<LocalDateTime> {
    override fun invoke(fabrikate: Fabrikate): LocalDateTime =
        LocalDateTime.ofInstant(InstantFabricator()(fabrikate), UTC)
}

class YearFabricator : Fabricator<Year> {
    override fun invoke(fabrikate: Fabrikate): Year =
        Year.of(fabrikate.config.random.nextInt(1970, 2030))
}

class MonthFabricator : Fabricator<Month> {
    override fun invoke(fabrikate: Fabrikate): Month = Month.values().random(fabrikate.config.random)
}

class YearMonthFabricator : Fabricator<YearMonth> {
    override fun invoke(fabrikate: Fabrikate): YearMonth =
        YearMonth.of(YearFabricator()(fabrikate).value, MonthFabricator()(fabrikate))
}

class OffsetDateTimeFabricator : Fabricator<OffsetDateTime> {
    override fun invoke(fabrikate: Fabrikate): OffsetDateTime =
        LocalDateTimeFabricator()(fabrikate).atOffset(UTC)
}

class OffsetTimeFabricator : Fabricator<OffsetTime> {
    override fun invoke(fabrikate: Fabrikate): OffsetTime =
        LocalTimeFabricator()(fabrikate).atOffset(UTC)
}

class ZonedDateTimeFabricator : Fabricator<ZonedDateTime> {
    override fun invoke(fabrikate: Fabrikate): ZonedDateTime =
        LocalDateTimeFabricator()(fabrikate).atZone(UTC)
}

class DateFabricator : Fabricator<Date> {
    override fun invoke(fabrikate: Fabrikate): Date = Date.from(InstantFabricator()(fabrikate))
}

class DurationFabricator : Fabricator<Duration> {
    override fun invoke(fabrikate: Fabrikate): Duration = Duration.ofDays(fabrikate.config.random.nextLong(1, 10))
}

class UUIDFabricator : Fabricator<UUID> {
    override fun invoke(fabrikate: Fabrikate): UUID =
        UUID(fabrikate.config.random.nextLong(), fabrikate.config.random.nextLong())
}

class UriFabricator : Fabricator<URI> {
    override fun invoke(fabrikate: Fabrikate): URI =
        URI.create("https://${StringFabricator()(fabrikate)}.com")
}

class UrlFabricator : Fabricator<URL> {
    override fun invoke(fabrikate: Fabrikate): URL =
        URL("https://${StringFabricator()(fabrikate).filter { it.isLetterOrDigit() }}.com")
}

class FileFabricator : Fabricator<File> {
    override fun invoke(fabrikate: Fabrikate): File = File.createTempFile("fabrikate", null).apply { deleteOnExit() }
}

class AnyFabricator(private val any: Any = "anything") : Fabricator<Any> {
    override fun invoke(fabrikate: Fabrikate): Any = any
}
