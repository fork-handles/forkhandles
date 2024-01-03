package dev.forkhandles.lens

import dev.forkhandles.data.MapDataContainer

class MapDataContainerTest : DataContainerContract() {

    class SubMap(propertySet: Map<String, Any?>) : MapDataContainer(propertySet), SubClassFields {
        override val stringField by field<String>()
        override val noSuchField by field<String>()
    }

    class MapBacked(propertySet: Map<String, Any?>) : MapDataContainer(propertySet), MainClassFields {
        override val stringField by field<String>()
        override val optionalField by field<String?>()
        override val booleanField by field<Boolean>()
        override val intField by field<Int>()
        override val longField by field<Long>()
        override val decimalField by field<Double>()
        override val notAStringField by field<String>()
        override val noSuchField by field<String>()
        override val listSubClassField by list(::SubMap)
        override val listStringsField by list(Any::toString)
        override val listIntsField by list<Any, Int> { it as Int }
        override val objectField by obj(::SubMap)
    }

    override fun container(input: Map<String, Any?>): MainClassFields = MapBacked(input)
}
