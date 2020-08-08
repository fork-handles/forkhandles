package parser4k

interface Parser<out T> {
    fun parse(input: Input): Output<T>?
}

data class Input(
    val value: String,
    val offset: Int = 0,
    val leftPayload: Any? = null
)

data class Output<out T>(
    val payload: T,
    val nextInput: Input
)

fun <T, R> Parser<T>.map(transform: (T) -> R) = object : Parser<R> {
    override fun parse(input: Input): Output<R>? {
        val (payload, nextInput) = this@map.parse(input) ?: return null
        return Output(transform(payload), nextInput)
    }
}