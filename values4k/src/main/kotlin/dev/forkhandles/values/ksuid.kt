package dev.forkhandles.values

import com.github.ksuid.Ksuid
import com.github.ksuid.KsuidGenerator
import java.time.Clock
import java.time.Instant
import java.util.Random

abstract class KsuidValue(value: Ksuid): AbstractValue<Ksuid>(value), Comparable<KsuidValue> {
    override fun compareTo(other: KsuidValue) = value.compareTo(other.value)
}

open class KsuidValueFactory<DOMAIN : Value<Ksuid>>(
    fn: (Ksuid) -> DOMAIN,
    showFn: (Ksuid) -> String = Ksuid::toString,
    validation: Validation<Ksuid> = { true }
) : ValueFactory<DOMAIN, Ksuid>(
    coerceFn = fn,
    validation = validation,
    parseFn = Ksuid::fromString,
    showFn = showFn,
)

val KsuidValue.instant: Instant get() = value.instant

private val defaultGenerator by lazy {
    KsuidGenerator(Random())
}

fun <DOMAIN : Value<Ksuid>> KsuidValueFactory<DOMAIN>.random(
    clock: Clock = Clock.systemUTC(),
    gen: KsuidGenerator = defaultGenerator
) = of(gen.newKsuid(clock.instant()))
