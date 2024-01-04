package dev.forkhandles.lens

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import dev.forkhandles.data.JacksonDataContainer
import java.math.BigDecimal

class JacksonDataContainerTest : DataContainerContract<JacksonDataContainerTest.SubNodeBacked>() {

    class SubNodeBacked(node: JsonNode) : JacksonDataContainer(node), SubClassFields {
        override var string by required<String>()
        override var noSuch by required<String>()
    }

    class NodeBacked(node: JsonNode) : JacksonDataContainer(node), MainClassFields<SubNodeBacked> {
        override var string by required<String>()
        override var boolean by required<Boolean>()
        override var int by required<Int>()
        override var long by required<Long>()
        override var double by required<Double>()
        override var decimal by required<BigDecimal>()
        override var notAString by required<String>()
        override var listSubClass by list(::SubNodeBacked)
        override var list by list<String>()
        override var listInts by list<Int>()
        override var listValue by list(MyType)
        override val listMapped by list(Int::toString)
        override var subClass by obj(::SubNodeBacked)
        override var value by required(MyType)
        override var mapped by required(String::toInt, Int::toString)

        override var optional by optional<String>()
        override var optionalMapped by optional(String::toInt, Int::toString)
        override var optionalList by optionalList<String>()
        override var optionalValueList by optionalList(MyType)
        override var optionalMappedList by optionalList(String::toInt, Int::toString)
        override var optionalSubClass by optionalObj(::SubNodeBacked)
        override var optionalSubClassList by optionalList(::SubNodeBacked)
        override var optionalValue by optional(MyType)
    }

    override fun container(input: Map<String, Any?>) = NodeBacked(ObjectMapper().valueToTree(input))
    override fun subContainer(input: Map<String, Any?>) =
        SubNodeBacked(ObjectMapper().valueToTree(input))
}
