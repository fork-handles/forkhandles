package dev.forkhandles.tuples

typealias Tuple2<T1, T2> = Pair<T1,T2>
val <T1,T2> Tuple2<T1, T2>.val1: T1 get() = first
val <T1,T2> Tuple2<T1, T2>.val2: T2 get() = second

typealias Tuple3<T1, T2, T3> = Triple<T1,T2,T3>
val <T1,T2,T3> Triple<T1,T2,T3>.val1: T1 get() = first
val <T1,T2,T3> Triple<T1,T2,T3>.val2: T2 get() = second
val <T1,T2,T3> Triple<T1,T2,T3>.val3: T3 get() = third

data class Tuple4<T1, T2, T3, T4>(val val1: T1, val val2: T2, val val3: T3, val val4: T4)

data class Tuple5<T1, T2, T3, T4, T5>(val val1: T1, val val2: T2, val val3: T3, val val4: T4, val val5: T5)

fun <T1,T2> tuple(val1: T1, val2: T2) = Tuple2(val1, val2)
fun <T1,T2, T3> tuple(val1: T1, val2: T2, val3: T3) = Tuple3(val1, val2, val3)
fun <T1,T2, T3, T4> tuple(val1: T1, val2: T2, val3: T3, val4: T4) = Tuple4(val1, val2, val3, val4)
fun <T1,T2, T3, T4, T5> tuple(val1: T1, val2: T2, val3: T3, val4: T4, val5: T5) = Tuple5(val1, val2, val3, val4, val5)
