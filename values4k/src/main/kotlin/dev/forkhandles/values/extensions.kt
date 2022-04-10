package dev.forkhandles.values

import java.math.BigDecimal
import java.math.BigInteger
import java.util.UUID
import kotlin.random.Random

/**
 * Random
 */
fun <DOMAIN : Value<Int>> IntValueFactory<DOMAIN>.random(random: Random = Random) = of(random.nextInt())
fun <DOMAIN : Value<Long>> LongValueFactory<DOMAIN>.random(random: Random = Random) = of(random.nextLong())
fun <DOMAIN : Value<Double>> DoubleValueFactory<DOMAIN>.random(random: Random = Random) = of(random.nextDouble())
fun <DOMAIN : Value<Float>> FloatValueFactory<DOMAIN>.random(random: Random = Random) = of(random.nextFloat())
fun <DOMAIN : Value<Boolean>> BooleanValueFactory<DOMAIN>.random(random: Random = Random) = of(random.nextBoolean())
fun <DOMAIN : Value<BigInteger>> BigIntegerValueFactory<DOMAIN>.random(random: Random = Random) = of(BigInteger.valueOf(random.nextLong()))
fun <DOMAIN : Value<BigDecimal>> BigDecimalValueFactory<DOMAIN>.random(random: Random = Random) = of(BigDecimal(random.nextDouble()))
fun <DOMAIN : Value<UUID>> UUIDValueFactory<DOMAIN>.random(random: Random = Random) = of(UUID(random.nextLong(), random.nextLong()))
