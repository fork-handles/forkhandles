package dev.forkhandles.data

import java.util.Properties

/**
 * Properties-based implementation of the DataContainer
 */
open class PropertiesDataContainer(input: Properties = Properties()) :
    DataContainer<Properties>(input, { content, it -> content.containsKey(it) },
        { content, it -> content[it] },
        { properties, name, value ->
            properties.remove(name)
            if(value != null) properties[name] = value
        }
    )
