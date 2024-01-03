package dev.forkhandles.lens

import dev.forkhandles.data.MapDataContainer

class MapDataContainerTest : DataContainerContract() {

    class SubMap(propertySet: Map<String, Any?>) : MapDataContainer(propertySet), SubClassFields {
        override val stringField by field<String>()
        override val noSuchField by field<String>()
    }

    class MapBacked(propertySet: Map<String, Any?>) : MapDataContainer(propertySet), MainClassFields {
        override val stringField by field<String>()
        override val booleanField by field<Boolean>()
        override val intField by field<Int>()
        override val longField by field<Long>()
        override val decimalField by field<Double>()
        override val notAStringField by field<String>()

        override val mappedField by field(String::toInt)

        override val listField by list<String>()
        override val listValueField by list(MyType)
        override val listSubClassField by list(::SubMap)
        override val listIntsField by list<Int>()

        override val objectField by obj(::SubMap)

        override val valueField by field(MyType)

        override val optionalField by field<String?>()
        override val optionalListField: List<String>? by list()
        override val optionalObjectField: SubMap? by obj(::SubMap)
        override val optionalValueField: MyType? by field(MyType)
    }

    override fun container(input: Map<String, Any?>): MainClassFields = MapBacked(input)
}
