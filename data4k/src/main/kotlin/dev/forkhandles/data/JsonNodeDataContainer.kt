package dev.forkhandles.data

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.BigIntegerNode
import com.fasterxml.jackson.databind.node.BinaryNode
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
import com.fasterxml.jackson.databind.node.ShortNode
import com.fasterxml.jackson.databind.node.TextNode
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Jackson JsonNode-based implementation of the DataContainer
 */
open class JsonNodeDataContainer(input: JsonNode = instance.objectNode()) :
    DataContainer<JsonNode>(
        input,
        { content, it -> content.has(it) },
        { content, it -> content[it]?.let(::nodeToValue) },
        { node: JsonNode, name, value ->
            (node as? ObjectNode)?.also { node.replace(name, value.toNode()) }
                ?: error("Invalid node type ${input::class.java}")
        }
    ) {

    companion object {
        private fun nodeToValue(input: JsonNode): Any? = when (input) {
            is NullNode -> null
            is TextNode -> input.textValue()
            is ArrayNode -> input.map(::nodeToValue)
            is ObjectNode -> input
            is BooleanNode -> input.booleanValue()
            is IntNode -> input.intValue()
            is LongNode -> input.longValue()
            is FloatNode -> input.floatValue()
            is DecimalNode -> input.decimalValue()
            is ShortNode -> input.shortValue()
            is DoubleNode -> input.doubleValue()
            is BigIntegerNode -> when {
                input.canConvertToInt() -> input.intValue()
                input.canConvertToLong() -> input.longValue()
                else -> input.bigIntegerValue()
            }
            is BinaryNode -> input.binaryValue()
            else -> error("Invalid node type ${input::class.java}")
        }

        private fun Any?.toNode(): JsonNode? =
            when (this) {
                null -> NullNode.instance
                is JsonNode -> this
                is DataContainer<*> -> unwrap().toNode()
                is Boolean -> if (this) TRUE else FALSE
                is Int -> IntNode(this)
                is Long -> LongNode(this)
                is Float -> FloatNode(this)
                is ByteArray -> BinaryNode(this)
                is Short -> ShortNode(this)
                is BigDecimal -> DecimalNode(this)
                is BigInteger -> BigIntegerNode(this)
                is Double -> DoubleNode(this)
                is String -> TextNode(this)
                is Iterable<*> -> ArrayNode(instance)
                    .also { map { if (it is JsonNode) it else it.toNode() }.forEach(it::add) }

                else -> error("Cannot set value of type ${this::class.java}")
            }
    }
}
