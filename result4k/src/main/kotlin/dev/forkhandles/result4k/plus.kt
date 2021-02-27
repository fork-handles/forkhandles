package dev.forkhandles.result4k

import dev.forkhandles.tuples.Tuple2
import dev.forkhandles.tuples.Tuple3
import dev.forkhandles.tuples.Tuple4
import dev.forkhandles.tuples.Tuple5
import dev.forkhandles.tuples.tuple

operator fun <A, B, EX> Result<A, EX>.plus(b: Result<B, EX>): Result<Tuple2<A, B>, EX> =
    zip(this, b) { a, b -> tuple(a, b) }

@JvmName("plus2")
operator fun <A, B, C, EX> Result<Tuple2<A, B>, EX>.plus(c: Result<C, EX>) =
    zip(this, c) { (a, b), c -> tuple(a, b, c) }

@JvmName("plus3")
operator fun <A, B, C, D, EX> Result<Tuple3<A, B, C>, EX>.plus(d: Result<D, EX>) =
    zip(this, d) { (a, b, c), d -> tuple(a, b, c, d) }

@JvmName("plus4")
operator fun <A, B, C, D, E, EX> Result<Tuple4<A, B, C, D>, EX>.plus(e: Result<E, EX>) =
    zip(this, e) { (a, b, c, d), e -> tuple(a, b, c, d, e) }

@JvmName("plus5")
operator fun <A, B, C, D, E, F, EX> Result<Tuple5<A, B, C, D, E>, EX>.plus(e: Result<F, EX>) =
    zip(this, e) { (a, b, c, d, e), f -> tuple(a, b, c, d, e, f) }

