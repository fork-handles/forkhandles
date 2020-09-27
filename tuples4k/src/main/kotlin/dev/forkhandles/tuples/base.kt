package dev.forkhandles.tuples

typealias Tuple2<T1, T2> = Pair<T1, T2>

val <T1, T2> Tuple2<T1, T2>.val1: T1 get() = first
val <T1, T2> Tuple2<T1, T2>.val2: T2 get() = second

typealias Tuple3<T1, T2, T3> = Triple<T1, T2, T3>

val <T1, T2, T3> Triple<T1, T2, T3>.val1: T1 get() = first
val <T1, T2, T3> Triple<T1, T2, T3>.val2: T2 get() = second
val <T1, T2, T3> Triple<T1, T2, T3>.val3: T3 get() = third

