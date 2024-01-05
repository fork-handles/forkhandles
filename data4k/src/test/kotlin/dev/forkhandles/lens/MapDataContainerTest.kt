package dev.forkhandles.lens

import dev.forkhandles.data.MapDataContainer
import java.math.BigDecimal

class MapDataContainerTest : DataContainerContract<MapDataContainerTest.SubMap>() {

    class SubMap(propertySet: Map<String, Any?>) : MapDataContainer(propertySet), SubClassFields {
        override var string by required<String>()
        override var noSuch by required<String>()
    }

    class MapBacked(map: Map<String, Any?>) : MapDataContainer(map), MainClassFields<SubMap> {
        override var string by required<String>()
        override var boolean by required<Boolean>()
        override var int by required<Int>()
        override var long by required<Long>()
        override var double by required<Double>()
        override var decimal by required<BigDecimal>()
        override var notAString by required<String>()

        override var mapped by required(String::toInt, Int::toString)

        override var list by list<String>()
        override var listValue by list(MyType)
        override var listSubClass by list(::SubMap)
        override var listInts by list<Int>()
        override val listMapped by list(Int::toString)

        override var subClass by obj(::SubMap)

        override var value by required(MyType)

        override var optional by optional<String>()
        override var optionalList by optionalList<String>()
        override var optionalValueList by optionalList(MyType)
        override var optionalSubClassList by optionalList(::SubMap)
        override var optionalSubClass by optionalObj(::SubMap)
        override var optionalValue by optional(MyType)
        override var optionalMapped by optional(String::toInt, Int::toString)
        override var optionalMappedList by optionalList(String::toInt, Int::toString)
    }

    override fun container(input: Map<String, Any?>) = MapBacked(input)

    override fun subContainer(input: Map<String, Any?>) = SubMap(input)
}
