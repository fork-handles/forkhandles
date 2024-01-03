package dev.forkhandles.lens

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import dev.forkhandles.data.JacksonDataContainer

class JacksonDataContainerTest : DataContainerContract() {

    class SubNodeBacked(node: JsonNode) : JacksonDataContainer(node), SubClassFields {
        override val stringField by field<String>()
        override val noSuchField by field<String>()
    }

    class NodeBacked(node: JsonNode) : JacksonDataContainer(node), MainClassFields {
        override val stringField by field<String>()
        override val optionalField by field<String?>()
        override val booleanField by field<Boolean>()
        override val intField by field<Int>()
        override val longField by field<Long>()
        override val decimalField by field<Double>()
        override val notAStringField by field<String>()
        override val noSuchField by field<String>()
        override val listSubClassField by list(::SubNodeBacked)
        override val listStringsField by list(Any::toString)
        override val listIntsField by list<Any, Int> { it as Int }
        override val objectField by obj(::SubNodeBacked)
    }

    override fun container(input: Map<String, Any?>) = NodeBacked(ObjectMapper().valueToTree(input))
}
