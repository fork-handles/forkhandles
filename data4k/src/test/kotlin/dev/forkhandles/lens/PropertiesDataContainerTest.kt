package dev.forkhandles.lens

import dev.forkhandles.data.PropertiesDataContainer
import dev.forkhandles.lens.ContainerMeta.bar
import dev.forkhandles.lens.ContainerMeta.foo
import java.util.Properties

class PropertiesDataContainerTest : SimpleClassFieldsContract {

    class PropertiesBacked(node: Properties) : PropertiesDataContainer(node), SimpleClassFields {
        override var standardField = "foobar"
        override var string by required<String>(foo, bar)
        override var list by list<String>(foo, bar)

        override var optional by optional<String>(foo, bar)
        override var optionalList by optionalList<String>(foo, bar)
    }

    override fun container(input: Map<String, Any?>) = PropertiesBacked(Properties().also { props ->
        input.entries
            .filter { it.value != null }
            .forEach {
                props.put(it.key, it.value.toString())
            }
    })
}
