package dev.forkhandles.lens

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.*

abstract class JacksonWrapper(node: JsonNode) :
    AbstractWrapper<JsonNode>(
        node,
        { content, it -> content.has(it) },
        { content, it -> content[it]?.let(::asValue) }
    ) {

    companion object {
        private fun asValue(jsonNode: JsonNode): Any? = when (jsonNode) {
            is BooleanNode -> jsonNode.booleanValue()
            is IntNode -> jsonNode.intValue()
            is LongNode -> jsonNode.longValue()
            is DecimalNode -> jsonNode.decimalValue()
            is DoubleNode -> jsonNode.doubleValue()
            is TextNode -> jsonNode.textValue()
            is ArrayNode -> jsonNode.map { asValue(it) }
            is ObjectNode -> jsonNode
            is NullNode -> null
            else -> error("Invalid node type $jsonNode")
        }

    }
}
