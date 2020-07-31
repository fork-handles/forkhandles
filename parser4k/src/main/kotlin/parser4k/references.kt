package parser4k

import kotlin.reflect.KProperty0

fun <T> ref(f: () -> Parser<T>): Parser<T> = object : Parser<T> {
    private val parser by lazy { f() }
    override fun invoke(input: Input) = parser.invoke(input)
}

fun <T> KProperty0<Parser<T>>.ref(): Parser<T> = ref { get() }

fun <T> nonRecursive(parser: Parser<T>): Parser<T> = object : Parser<T> {
    val offsets: HashSet<Int> = HashSet()

    override fun invoke(input: Input): Output<T>? {
        if (!offsets.add(input.offset)) return null
        val output = parser.invoke(input)
        offsets.remove(input.offset)
        return output
    }
}
