package dev.forkhandles.data

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.*

/**
 * Jackson JsonNode-based implementation of the DataContainer
 */
abstract class JacksonDataContainer(input: JsonNode) :
    DataContainer<JsonNode>(
        input,
        { content, it -> content.has(it) },
        { content, it -> content[it]?.let(Companion::nodeToValue) }
    ) {

    companion object {
        private fun nodeToValue(input: JsonNode): Any? = when (input) {
            is BooleanNode -> input.booleanValue()
            is IntNode -> input.intValue()
            is LongNode -> input.longValue()
            is DecimalNode -> input.decimalValue()
            is DoubleNode -> input.doubleValue()
            is TextNode -> input.textValue()
            is ArrayNode -> input.map(::nodeToValue)
            is ObjectNode -> input
            is NullNode -> null
            else -> error("Invalid node type $input")
        }
    }
}
