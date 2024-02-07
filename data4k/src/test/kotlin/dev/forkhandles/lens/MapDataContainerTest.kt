package dev.forkhandles.lens

import dev.forkhandles.data.MapDataContainer
import dev.forkhandles.lens.ContainerMeta.bar
import dev.forkhandles.lens.ContainerMeta.foo
import java.math.BigDecimal
import java.math.BigInteger

class MapDataContainerTest : ComplexClassFieldsDataContainerContract<MapDataContainerTest.SubMap> {

    class SubMap(propertySet: Map<String, Any?>) : MapDataContainer(propertySet), SubClassFields {
        override var string by required<String>()
        override var noSuch by required<String>()
    }

    class MapBacked(map: Map<String, Any?>) : MapDataContainer(map), PolymorphicClassFields, ComplexClassFields<SubMap> {
        override var standardField = "foobar"
        override var string by required<String>(foo, bar)
        override var boolean by required<Boolean>(foo, bar)
        override var int by required<Int>(foo, bar)
        override var long by required<Long>(foo, bar)
        override var double by required<Double>(foo, bar)
        override var decimal by required<BigDecimal>(foo, bar)
        override var notAnInt by required<Int>(foo, bar)
        override var bigInt by required<BigInteger>(foo, bar)

        override var mapped by required(String::toInt, Int::toString, foo, bar)

        override var list by list<String>(foo, bar)
        override var listValue by list(MyType, foo, bar)
        override var listSubClass by list(::SubMap, foo, bar)
        override var listInts by list<Int>(foo, bar)
        override val listMapped by list(Int::toString, foo, bar)

        override var subClass by obj(::SubMap, foo, bar)

        override var value by required(MyType, foo, bar)

        override var optional by optional<String>(foo, bar)
        override var optionalList by optionalList<String>(foo, bar)
        override var optionalValueList by optionalList(MyType, foo, bar)
        override var optionalSubClassList by optionalList(::SubMap, foo, bar)
        override var optionalSubClass by optionalObj(::SubMap, foo, bar)
        override var optionalValue by optional(MyType, foo, bar)
        override var optionalMapped by optional(String::toInt, Int::toString, foo, bar)
        override var optionalMappedList by optionalList(String::toInt, Int::toString, foo, bar)
    }

    override fun container(input: Map<String, Any?>) = MapBacked(input)

    override fun subContainer(input: Map<String, Any?>) = SubMap(input)
}
