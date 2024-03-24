package dev.forkhandles.lens

import dev.forkhandles.data.MapDataContainer
import dev.forkhandles.lens.ContainerMeta.bar
import dev.forkhandles.lens.ContainerMeta.foo
import java.math.BigDecimal
import java.math.BigInteger

class GrandchildMap(propertySet: Map<String, Any?>) : MapDataContainer(propertySet), GrandchildFields {
    override var long by required<Long>()
}

class ChildMap(propertySet: Map<String, Any?>) : MapDataContainer(propertySet), ChildFields<GrandchildMap> {
    override var string by required<String>()
    override var noSuch by required<String>()
    override var grandchild by requiredObj(::GrandchildMap)
}

class MapBacked(map: Map<String, Any?>) : MapDataContainer(map), MainClassFields<ChildMap, GrandchildMap, MutableMap<String, Any?>> {
    override var standardField = "foobar"
    override var string by required<String>(foo, bar)
    override var boolean by required<Boolean>(foo, bar)
    override var int by required<Int>(foo, bar)
    override var long by required<Long>(foo, bar)
    override var double by required<Double>(foo, bar)
    override var decimal by required<BigDecimal>(foo, bar)
    override var notAString by required<String>(foo, bar)
    override var bigInt by required<BigInteger>(foo, bar)

    override var mapped by required(String::toInt, Int::toString, foo, bar)

    override var list by requiredList<String>(foo, bar)
    override var listValue by requiredList(MyType, foo, bar)
    override var listSubClass by requiredList(::ChildMap, foo, bar)
    override var listInts by requiredList<Int>(foo, bar)
    override val listMapped by requiredList(Int::toString, foo, bar)

    override var subClass by requiredObj(::ChildMap, foo, bar)

    override var value by required(MyType, foo, bar)
    override var requiredData by requiredData(foo, bar)

    override var optional by optional<String>(foo, bar)
    override var optionalList by optionalList<String>(foo, bar)
    override var optionalValueList by optionalList(MyType, foo, bar)
    override var optionalSubClassList by optionalList(::ChildMap, foo, bar)
    override var optionalSubClass by optionalObj(::ChildMap, foo, bar)
    override var optionalValue by optional(MyType, foo, bar)
    override var optionalMapped by optional(String::toInt, Int::toString, foo, bar)
    override var optionalMappedList by optionalList(String::toInt, Int::toString, foo, bar)
    override var optionalData by optionalData(foo, bar)
}

class MapDataContainerTest : DataContainerContract<ChildMap, GrandchildMap, MutableMap<String, Any?>>() {
    override fun data(input: Map<String, Any?>) = input.toMutableMap()
    override fun container(input: Map<String, Any?>) = MapBacked(data(input))
    override fun childContainer(input: Map<String, Any?>) = ChildMap(data(input))
    override fun grandchildContainer(input: Map<String, Any?>) = GrandchildMap(data(input))
}
