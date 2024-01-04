package dev.forkhandles.data

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.BooleanNode.FALSE
import com.fasterxml.jackson.databind.node.BooleanNode.TRUE
import com.fasterxml.jackson.databind.node.DecimalNode
import com.fasterxml.jackson.databind.node.DoubleNode
import com.fasterxml.jackson.databind.node.FloatNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory.instance
import com.fasterxml.jackson.databind.node.LongNode
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import java.math.BigDecimal

/**
 * Jackson JsonNode-based implementation of the DataContainer
 */
abstract class JsonNodeDataContainer(input: JsonNode) :
    DataContainer<JsonNode>(
        input,
        { content, it -> content.has(it) },
        { content, it -> content[it]?.let(Companion::nodeToValue) },
        { node: JsonNode, name, value ->
            (node as? ObjectNode)?.also { node.set<JsonNode>(name, value.toNode()) }
                ?: error("Invalid node type ${input::class.java}")
        }
    ) {

    companion object {
        private fun nodeToValue(input: JsonNode): Any? = when (input) {
            is BooleanNode -> input.booleanValue()
            is IntNode -> input.intValue()
            is LongNode -> input.longValue()
            is FloatNode -> input.floatValue()
            is DecimalNode -> input.decimalValue()
            is DoubleNode -> input.doubleValue()
            is TextNode -> input.textValue()
            is ArrayNode -> input.map(::nodeToValue)
            is ObjectNode -> input
            is NullNode -> null
            else -> error("Invalid node type ${input::class.java}")
        }

        private fun Any?.toNode(): JsonNode? =
            when (this) {
                null -> NullNode.instance
                is JsonNode -> this
                is DataContainer<*> -> data.toNode()
                is Boolean -> if (this) TRUE else FALSE
                is Int -> IntNode(this)
                is Long -> LongNode(this)
                is Float -> FloatNode(this)
                is BigDecimal -> DecimalNode(this)
                is Double -> DoubleNode(this)
                is String -> TextNode(this)
                is Iterable<*> -> ArrayNode(instance)
                    .also { map { if (it is JsonNode) it else it.toNode() }.forEach(it::add)
                }

                else -> error("Cannot set value of type ${this::class.java}")
            }
    }
}
