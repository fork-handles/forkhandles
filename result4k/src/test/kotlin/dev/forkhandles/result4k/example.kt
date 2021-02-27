package dev.forkhandles.result4k

val _1 = Success(1)
val _2 = Success(2)
val _3 = Success(3)

fun main() {
    // option 1
    val (a1, b1) = _1
        .then1 { _2 }
        .then2 { _3 }
        .orThrow()

    // option 2
    val rA = _1.flatMap { _2 }
    val rB = _2.flatMap { _3 }

    val (a2, b2) = (rA + rB).orThrow()
}
