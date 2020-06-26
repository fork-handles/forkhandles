package dev.forkhandles.tuples

fun <T1, T2> tupleFlat(val1: T1, val2: T2) = Tuple2(val1, val2)
fun <T1, T2, T3> tupleFlat(val2: T1, t: Tuple2<T2, T3>) = Tuple3(val2, t.val1, t.val2)
fun <T1, T2, T3, T4> tupleFlat(val3: T1, t: Tuple3<T2, T3, T4>) = Tuple4(val3, t.val1, t.val2, t.val3)
fun <T1, T2, T3, T4, T5> tupleFlat(val4: T1, t: Tuple4<T2, T3, T4, T5>) = Tuple5(val4, t.val1, t.val2, t.val3, t.val4)

fun <T1, T2, T3> tupleFlat(t1: Tuple2<T1, T2>, val3: T3) = Tuple3(t1.val1, t1.val2, val3)
fun <T1, T2, T3, T4> tupleFlat(t1: Tuple2<T1, T2>, t2: Tuple2<T3, T4>) = Tuple4(t1.val1, t1.val2, t2.val1, t2.val2)
fun <T1, T2, T3, T4, T5> tupleFlat(t1: Tuple2<T1, T2>, t2: Tuple3<T3, T4, T5>) = Tuple5(t1.val1, t1.val2, t2.val1, t2.val2, t2.val3)

fun <T1, T2, T3, T4> tupleFlat(t1: Tuple3<T1, T2, T3>, val4: T4) = Tuple4(t1.val1, t1.val2, t1.val3, val4)
fun <T1, T2, T3, T4, T5> tupleFlat(t1: Tuple3<T1, T2, T3>, t2: Tuple2<T4, T5>) = Tuple5(t1.val1, t1.val2, t1.val3, t2.val1, t2.val2)

fun <T1, T2, T3, T4, T5> tupleFlat(t1: Tuple4<T1, T2, T3, T4>, val5: T5) = Tuple5(t1.val1, t1.val2, t1.val3, t1.val4, val5)
