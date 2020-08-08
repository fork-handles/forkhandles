package parser4k

import parser4k.OutputCache.Key

fun <T> Parser<T>.with(outputCache: OutputCache<T>): Parser<T> = object : Parser<T> {
    override fun parse(input: Input): Output<T>? {
        val parser = this@with
        val key = Key(parser, input.offset, input.leftPayload)
        if (outputCache.contains(key)) return outputCache[key]
        outputCache[key] = null // Mark parser at offset as work-in-progress

        val output = parser.parse(input)

        outputCache[key] = output
        return output
    }
}

fun <T> Parser<T>.reset(outputCache: OutputCache<T>) = object : Parser<T> {
    private var depth = 0

    override fun parse(input: Input): Output<T>? {
        return try {
            depth++
            this@reset.parse(input)
        } finally {
            depth--
            if (depth == 0) outputCache.clear()
        }
    }
}

class OutputCache<T> {
    private val map = HashMap<Key<T>, Output<T>?>()

    fun contains(key: Key<T>) = map.containsKey(key)

    operator fun get(key: Key<T>) = map[key]

    operator fun set(key: Key<T>, output: Output<T>?) {
        map[key] = output
    }

    fun clear() = map.clear()

    data class Key<T>(val parser: Parser<T>, val offset: Int, val leftPayload: Any?)
}
