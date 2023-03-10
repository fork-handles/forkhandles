# Parser4k

<a href="https://mvnrepository.com/artifact/dev.forkhandles"><img alt="Download" src="https://img.shields.io/maven-central/v/dev.forkhandles/forkhandles-bom"></a>
[![.github/workflows/build.yaml](https://github.com/fork-handles/forkhandles/actions/workflows/build.yaml/badge.svg)](https://github.com/fork-handles/forkhandles/actions/workflows/build.yaml)
<a href="https://codecov.io/gh/fork-handles/forkhandles"><img src="https://codecov.io/gh/fork-handles/forkhandles/branch/trunk/graph/badge.svg"/></a>
<a href="http//www.apache.org/licenses/LICENSE-2.0"><img alt="GitHub license" src="https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat"></a>
<a href="http://kotlinlang.org"><img alt="kotlin" src="https://img.shields.io/badge/kotlin-1.8-blue.svg"></a>
<a href="https://codebeat.co/projects/github-com-fork-handles-forkhandles-trunk"><img alt="codebeat badge" src="https://codebeat.co/badges/5b369ed4-af27-46f4-ad9c-a307d900617e"></a>

Recursive descent parser combinator library for Kotlin with support for [left recursion](https://en.wikipedia.org/wiki/Left_recursion).
It aims to be:
- **simple** - only three core concepts
- **easy to use** - you can quickly figure out how to write a parser for a small language
- **production-ready** - enough functionality and performance for real-world applications

## Installation
In Gradle, install the ForkHandles BOM and then this module in the dependency block:
```kotlin
implementation(platform("dev.forkhandles:forkhandles-bom:X.Y.Z"))
implementation("dev.forkhandles:parser4k")
```
 
### Examples
```kotlin
object MinimalCalculator {
    val cache = OutputCache<BigDecimal>()
    fun binaryExpr(s: String) = inOrder(ref { expr }, token(s), ref { expr })

    val number = oneOrMore(oneOf('0'..'9')).map { it.joinToString("").toBigDecimal() }.with(cache)
    val paren = inOrder(token("("), ref { expr }, token(")")).skipWrapper().with(cache)

    val power = binaryExpr("^").map { (l, _, r) -> l.pow(r.toInt()) }.with(cache)
    val divide = binaryExpr("/").mapLeftAssoc { (l, _, r) -> l.divide(r) }.with(cache)
    val multiply = binaryExpr("*").mapLeftAssoc { (l, _, r) -> l * r }.with(cache)

    val minus = binaryExpr("-").mapLeftAssoc { (l, _, r) -> l - r }.with(cache)
    val plus = binaryExpr("+").mapLeftAssoc { (l, _, r) -> l + r }.with(cache)

    val expr: Parser<BigDecimal> = oneOfWithPrecedence(
        oneOf(plus, minus),
        oneOf(multiply, divide),
        power,
        paren.nestedPrecedence(),
        number
    ).reset(cache)

    fun evaluate(s: String) = s.parseWith(expr)
}
```
See also:
 - [Full source code for the calculator parser](src/test/kotlin/parser4k/examples/calculator.kt)
 - [Json parser](src/test/kotlin/parser4k/examples/json/json-parser.kt)
 - [Expression language parser](src/test/kotlin/parser4k/examples/expression-lang.kt)


## Core concepts
There are only three concepts you need to know:
 - `Parser` is any object which takes `Input`, attempts to extract some useful data from it and 
   returns `Output` if successful or `null` if `Parser` wasn't able to consume any data.
   `Parser`s can be mapped with `.map()` function similar to how collections are mapped in Kotlin.
 - `Input` is an immutable object which contains input string and offset, where offset indicates how many characters has been consumed from the string.
 - `Output` is an immutable object which contains payload (i.e. useful data extracted from input) and `Input` with shifted offset (to be used by the next `Parser`).
 
```kotlin
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
```

## Core parsers
 - `str()` - consumes input from the current offset if it's equal to the specified string
 - `repeat()`, `zeroOrMore()`, `oneOrMore()`, `optional()` - applies specified parser multiple times
 - `inOrder()` - apply each of the specified parsers sequentially ("sequence" could be a good name but it's already used in Kotlin)
 - `ref { parser }`, `::parser.ref()` - workaround to forward-reference fields in Kotlin
 - `oneOf()` - apply the first matching parser
 - `oneOfLongest()` - apply the parser which can consume the longest part of the input
 - `oneOfWithPrecedence()` - apply the first matching parser with arguments indices as operator precedence, 
   where high precedence means that parser will attempt to consume input at the "innermost" level and will produce payload before parsers with lower precedence.
 - `.nestedPrecedence()` - can be used in combination with `oneOfWithPrecedence()` on parsers which have nested precedence (e.g. parenthesis)
 - `.mapLeftAssoc()` - can be used with `inOrder()` to produce left-associative payload
 - `.with(outputCache)` - cache parsers output (should be used with any non-toy parser combinator to avoid exponential time complexity)
 - `.reset(outputCache)` - should be used on the main parser to reset output cache after processing each input
 - `.with("parserId", parsingLog)` - wrap parser with a logger (can be useful for debugging/understanding what parser is doing, e.g. see [log-tests.kt](src/test/kotlin/parser4k/log-tests.kt) 


## Common parsers
 - `.joinedWith()` - wrap parser so that it matches multiple times with specified separator (e.g. a list of arguments to a function separated by commas)
 - `inOrder(...).skipFirst()`, `.skipLast()`, `.skipWrapper()` - skip first, last or both first and last items from payload
 - `.asBinary()` - makes function with 2 arguments usable as a binary operator, 
   e.g. `inOrder(ref { expr }, token("+"), ref { expr }).mapLeftAssoc(::Plus.asBinary())`
 - `commonparsers.Tokens` - parsers for basic bits of input (usually called "tokens"), e.g. `whitespace` or `number`


## See also
 - [Parser combinator koans](https://github.com/dkandalov/parser-combinator-koans)
