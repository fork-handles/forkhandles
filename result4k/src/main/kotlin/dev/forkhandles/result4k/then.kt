package dev.forkhandles.result4k

import dev.forkhandles.tuples.Tuple2
import dev.forkhandles.tuples.Tuple3
import dev.forkhandles.tuples.Tuple4
import dev.forkhandles.tuples.Tuple5

fun <T1, T2, E> Result<T1, E>.then1(fn: (T1) -> Result<T2, E>) = this + flatMap(fn)

fun <T1, T2, T3, E> Result<Tuple2<T1, T2>, E>.then2(fn: (T2) -> Result<T3, E>) =
    this + flatMap { (_, next) -> fn(next) }

fun <T1, T2, T3, T4, E> Result<Tuple3<T1, T2, T3>, E>.then3(fn: (T3) -> Result<T4, E>) =
    this + flatMap { (_, _, next) -> fn(next) }

fun <T1, T2, T3, T4, T5, E> Result<Tuple4<T1, T2, T3, T4>, E>.then4(fn: (T4) -> Result<T5, E>) =
    this + flatMap { (_, _, _, next) -> fn(next) }

fun <T1, T2, T3, T4, T5, T6, E> Result<Tuple5<T1, T2, T3, T4, T5>, E>.then5(fn: (T5) -> Result<T6, E>) =
    this + flatMap { (_, _, _, _, next) -> fn(next) }
