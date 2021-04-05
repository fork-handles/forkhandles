package fabrikate4k

import fabrikate4k.fabricators.InstanceFabricator
import fabrikate4k.fabricators.InstanceFabricatorConfig
import fabrikate4k.fabricators.getKType

object Fabrikate {

    inline fun <reified T : Any> random(config: InstanceFabricatorConfig = InstanceFabricatorConfig()): T =
        InstanceFabricator(config).makeRandomInstance(T::class, getKType<T>()) as T

}
