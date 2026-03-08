package dev.forkhandles.data

import tools.jackson.databind.JsonNode
import tools.jackson.databind.node.ArrayNode
import tools.jackson.databind.node.BigIntegerNode
import tools.jackson.databind.node.BinaryNode
import tools.jackson.databind.node.BooleanNode
import tools.jackson.databind.node.BooleanNode.FALSE
import tools.jackson.databind.node.BooleanNode.TRUE
import tools.jackson.databind.node.DecimalNode
import tools.jackson.databind.node.DoubleNode
import tools.jackson.databind.node.FloatNode
import tools.jackson.databind.node.IntNode
import tools.jackson.databind.node.JsonNodeFactory.instance
import tools.jackson.databind.node.LongNode
import tools.jackson.databind.node.NullNode
import tools.jackson.databind.node.ObjectNode
import tools.jackson.databind.node.ShortNode
import tools.jackson.databind.node.StringNode
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Jackson JsonNode-based implementation of the DataContainer
 */
open class JsonNodeDataContainer(input: JsonNode) :
    DataContainer<JsonNode>(
        input,
        { content, it -> content.has(it) },
        { content, it -> content[it]?.let(::nodeToValue) },
        { node, name, value ->
            (node as? ObjectNode)?.also { node.replace(name, value.toNode()) }
                ?: error("Invalid node type ${input::class.java}")
        }
    ) {
    companion object {
        private fun Any?.toNode(): JsonNode? = when (this) {
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
                is String -> StringNode(this)
                is Iterable<*> -> ArrayNode(instance)
                    .also { map { if (it is JsonNode) it else it.toNode() }.forEach(it::add) }

                else -> error("Invalid node type ${this::class.java}")
            }

        private fun nodeToValue(input: JsonNode): Any? = when (input) {
            is NullNode -> null
            is StringNode -> input.stringValue()
            is ArrayNode -> input.elements().map { nodeToValue(it) }
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
    }
}
