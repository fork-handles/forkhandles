package fabrikate4k

import fabrikate4k.fabricators.InstanceFabricator
import fabrikate4k.fabricators.InstanceFabricatorConfig
import fabrikate4k.fabricators.getKType

open class Fabrikate(val config: InstanceFabricatorConfig = InstanceFabricatorConfig()) {

    inline fun <reified T : Any> random(): T =
        InstanceFabricator(config).makeRandomInstance(T::class, getKType<T>()) as T
}
