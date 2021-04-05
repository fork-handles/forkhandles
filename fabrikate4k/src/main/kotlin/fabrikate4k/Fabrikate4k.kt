package fabrikate4k

import fabrikate4k.fabricators.InstanceFabricator
import fabrikate4k.fabricators.FabricatorConfig
import fabrikate4k.fabricators.getKType

open class Fabrikate(val config: FabricatorConfig = FabricatorConfig()) {

    inline fun <reified T : Any> random(): T =
        InstanceFabricator(config).makeRandomInstance(T::class, getKType<T>()) as T
}
