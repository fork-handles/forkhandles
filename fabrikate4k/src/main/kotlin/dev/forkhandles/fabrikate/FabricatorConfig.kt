package dev.forkhandles.fabrikate

import kotlin.random.Random
import kotlin.reflect.KClass

class FabricatorConfig(
    seed: Int = 861_084_310,
    val collectionSizes: IntRange = 1..5,
) {
    val random: Random = Random(seed)
    val mappings = mutableMapOf<KClass<*>, Fabricator<*>>()

    fun withStandardMappings() = apply {
        register(StringFabricator(random = random))
        register(LongFabricator(random))
        register(IntFabricator(random))
        register(DoubleFabricator(random))
        register(FloatFabricator(random))
        register(CharFabricator(random = random))
        register(BytesFabricator(random = random))
        register(BigIntegerFabricator(random = random))
        register(BigDecimalFabricator(random = random))
        register(InstantFabricator(random))
        register(LocalDateFabricator(random))
        register(LocalTimeFabricator(random))
        register(LocalDateTimeFabricator(random))
        register(OffsetDateTimeFabricator(random))
        register(OffsetTimeFabricator(random))
        register(ZonedDateTimeFabricator(random))
        register(DurationFabricator(random))
        register(DateFabricator(random))
        register(UriFabricator(random))
        register(UrlFabricator(random))
        register(FileFabricator())
        register(UUIDFabricator())
        register(AnyFabricator())
    }

    inline fun <reified T : Any> register(noinline fabricator: Fabricator<T>) = apply {
        mappings[T::class] = fabricator
    }
}
