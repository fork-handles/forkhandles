package dev.forkhandles.fabrikate

import dev.forkhandles.fabrikate.FabricatorConfig.NullableStrategy.RandomlySetToNull
import kotlin.random.Random
import kotlin.reflect.KClass

data class FabricatorConfig(
    val random: Random = Random(861_084_310),
    val collectionSizes: IntRange = 1..5,
    val nullableStrategy: NullableStrategy = RandomlySetToNull,
    val mappings: Map<KClass<*>, Fabricator<*>> = emptyMap(),
) {
    companion object {
        operator fun invoke(
            seed: Int = 861_084_310,
            collectionSizes: IntRange = 1..5,
            nullableStrategy: NullableStrategy = RandomlySetToNull,
            mappings: Map<KClass<*>, Fabricator<*>> = emptyMap(),
        ): FabricatorConfig = FabricatorConfig(Random(seed), collectionSizes, nullableStrategy, mappings)
    }

    enum class NullableStrategy { RandomlySetToNull, NeverSetToNull, AlwaysSetToNull }

    fun withStandardMappings(): FabricatorConfig = withMappings {
        registerStandardMappings()
    }

    fun withMappings(fn: MutableMap<KClass<*>, Fabricator<*>>.() -> Unit): FabricatorConfig = copy(
        mappings = mappings.toMutableMap().apply(fn),
    )

    inline fun <reified T : Any> withMapping(fabricator: Fabricator<T>): FabricatorConfig = withMappings {
        register(fabricator)
    }

    inline fun <reified T : Any> register(fabricator: Fabricator<T>): FabricatorConfig =
        withMapping(fabricator)
}

inline fun <reified T> MutableMap<KClass<*>, Fabricator<*>>.register(fabricator: Fabricator<T>) {
    this[T::class] = fabricator
}

fun MutableMap<KClass<*>, Fabricator<*>>.registerStandardMappings() {
    register(StringFabricator())
    register(LongFabricator())
    register(BooleanFabricator())
    register(ByteFabricator())
    register(IntFabricator())
    register(DoubleFabricator())
    register(FloatFabricator())
    register(CharFabricator())
    register(BytesFabricator())
    register(BigIntegerFabricator())
    register(BigDecimalFabricator())
    register(InstantFabricator())
    register(LocalDateFabricator())
    register(LocalTimeFabricator())
    register(LocalDateTimeFabricator())
    register(YearMonthFabricator())
    register(OffsetDateTimeFabricator())
    register(OffsetTimeFabricator())
    register(ZonedDateTimeFabricator())
    register(DurationFabricator())
    register(DateFabricator())
    register(UriFabricator())
    register(UrlFabricator())
    register(FileFabricator())
    register(UUIDFabricator())
    register(AnyFabricator())
}
