package dev.forkhandles.fabrikate

import kotlin.reflect.KClass

class Fabrikate(
    val config: FabricatorConfig = FabricatorConfig().withStandardMappings(),
) {
    companion object {
        fun withMappings(fn: MutableMap<KClass<*>, Fabricator<*>>.() -> Unit): Fabrikate =
            Fabrikate(FabricatorConfig().withMappings(fn))

        fun withStandardMappings(): Fabrikate =
            withMappings { registerStandardMappings() }
    }

    inline fun <reified T : Any> random(config: FabricatorConfig = this.config): T {
        val fabrikate = if (config == this.config) this else Fabrikate(config)
        return InstanceFabricator(fabrikate).makeRandomInstance(T::class, getKType<T>()) as T
    }

    inline fun <reified T : Any> random(noinline configFn: (FabricatorConfig) -> FabricatorConfig): T =
        withConfig(configFn).random()

    inline fun <reified T : Any> random(fabricator: Fabricator<T>): T = fabricator(this)

    inline operator fun <reified T : Any> invoke(
        fabricator: Fabricator<T>,
        configFn: (FabricatorConfig) -> FabricatorConfig = { it },
    ): T = random(configFn(config).withMapping(fabricator))

    fun withConfig(configFn: (FabricatorConfig) -> FabricatorConfig): Fabrikate =
        Fabrikate(configFn(config))

    inline fun <reified T : Any> withMapping(fn: Fabricator<T>): Fabrikate =
        Fabrikate(config.withMapping(fn))

    fun withMappings(fn: MutableMap<KClass<*>, Fabricator<*>>.() -> Unit): Fabrikate =
        Fabrikate(config.withMappings(fn))
}

inline operator fun <reified T : Any> Fabricator<T>.invoke(configFn: (FabricatorConfig) -> FabricatorConfig = { it }): T =
    invoke(Fabrikate(), configFn)

inline operator fun <reified T : Any> Fabricator<T>.invoke(
    fabrikate: Fabrikate,
    configFn: (FabricatorConfig) -> FabricatorConfig = { it },
): T = fabrikate(this, configFn)
