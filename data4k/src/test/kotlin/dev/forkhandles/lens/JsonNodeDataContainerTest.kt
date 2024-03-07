package dev.forkhandles.lens

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import dev.forkhandles.data.JsonNodeDataContainer
import dev.forkhandles.lens.ContainerMeta.bar
import dev.forkhandles.lens.ContainerMeta.foo
import java.math.BigDecimal
import java.math.BigInteger

class GrandchildNode(node: JsonNode) : JsonNodeDataContainer(node), GrandchildFields {
    override var long by required<Long>()
}

class ChildNode(node: JsonNode) : JsonNodeDataContainer(node), ChildFields<GrandchildNode> {
    override var string by required<String>()
    override var noSuch by required<String>()
    override var grandchild by obj(::GrandchildNode)
}

class TopNode(node: JsonNode) : JsonNodeDataContainer(node), MainClassFields<ChildNode, GrandchildNode, JsonNode> {
    override var standardField = "foobar"
    override var string by required<String>(foo, bar)
    override var boolean by required<Boolean>(foo, bar)
    override var int by required<Int>(foo, bar)
    override var long by required<Long>(foo, bar)
    override var double by required<Double>(foo, bar)
    override var decimal by required<BigDecimal>(foo, bar)
    override var bigInt by required<BigInteger>(foo, bar)
    override var notAString by required<String>(foo, bar)
    override var listSubClass by list(::ChildNode, foo, bar)
    override var list by list<String>(foo, bar)
    override var listInts by list<Int>(foo, bar)
    override var listValue by list(MyType, foo, bar)
    override val listMapped by list(Int::toString, foo, bar)
    override var subClass by obj(::ChildNode, foo, bar)
    override var value by required(MyType, foo, bar)
    override var mapped by required(String::toInt, Int::toString, foo, bar)
    override var requiredData by data(foo, bar)

    override var optional by optional<String>(foo, bar)
    override var optionalMapped by optional(String::toInt, Int::toString, foo, bar)
    override var optionalList by optionalList<String>(foo, bar)
    override var optionalValueList by optionalList(MyType, foo, bar)
    override var optionalMappedList by optionalList(String::toInt, Int::toString, foo, bar)
    override var optionalSubClass by optionalObj(::ChildNode, foo, bar)
    override var optionalSubClassList by optionalList(::ChildNode, foo, bar)
    override var optionalValue by optional(MyType, foo, bar)
    override var optionalData by optionalData(foo, bar)
}

class JsonNodeDataContainerTest : DataContainerContract<ChildNode, GrandchildNode, JsonNode>() {
    override fun data(input: Map<String, Any?>): JsonNode = ObjectMapper().valueToTree(input)

    override fun container(input: Map<String, Any?>) = TopNode(ObjectMapper().valueToTree(input))

    override fun childContainer(input: Map<String, Any?>) =
        ChildNode(data(input))

    override fun grandchildContainer(input: Map<String, Any?>) =
        GrandchildNode(data(input))
}
