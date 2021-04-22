package dev.forkhandles.fabrikate

class Fabrikate(val config: FabricatorConfig = FabricatorConfig().withStandardMappings()) {
    inline fun <reified T : Any> random(): T =
        InstanceFabricator(config).makeRandomInstance(T::class, getKType<T>()) as T
}
