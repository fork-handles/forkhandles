package dev.forkhandles.result4k

inline fun <T1, U, E> zip(
    r1: Result<T1, E>,
    transform: (T1) -> U
): Result<U, E> =
    r1.map(transform)

inline fun <T1, T2, U, E> zip(
    r1: Result<T1, E>,
    r2: Result<T2, E>,
    transform: (T1, T2) -> U
): Result<U, E> =
    r1.flatMap { v1 ->
        r2.map { v2 ->
            transform(v1, v2)
        }
    }

inline fun <T1, T2, T3, U, E> zip(
    r1: Result<T1, E>,
    r2: Result<T2, E>,
    r3: Result<T3, E>,
    transform: (T1, T2, T3) -> U
): Result<U, E> =
    r1.flatMap { v1 ->
        r2.flatMap { v2 ->
            r3.map { v3 ->
                transform(v1, v2, v3)
            }
        }
    }

inline fun <T1, T2, T3, T4, U, E> zip(
    r1: Result<T1, E>,
    r2: Result<T2, E>,
    r3: Result<T3, E>,
    r4: Result<T4, E>,
    transform: (T1, T2, T3, T4) -> U
): Result<U, E> =
    r1.flatMap { v1 ->
        r2.flatMap { v2 ->
            r3.flatMap { v3 ->
                r4.map { v4 ->
                    transform(v1, v2, v3, v4)
                }
            }
        }
    }

inline fun <T1, T2, T3, T4, T5, U, E> zip(
    r1: Result<T1, E>,
    r2: Result<T2, E>,
    r3: Result<T3, E>,
    r4: Result<T4, E>,
    r5: Result<T5, E>,
    transform: (T1, T2, T3, T4, T5) -> U
): Result<U, E> =
    r1.flatMap { v1 ->
        r2.flatMap { v2 ->
            r3.flatMap { v3 ->
                r4.flatMap { v4 ->
                    r5.map { v5 ->
                        transform(v1, v2, v3, v4, v5)
                    }
                }
            }
        }
    }

inline fun <T1, T2, T3, T4, T5, T6, U, E> zip(
    r1: Result<T1, E>,
    r2: Result<T2, E>,
    r3: Result<T3, E>,
    r4: Result<T4, E>,
    r5: Result<T5, E>,
    r6: Result<T6, E>,
    transform: (T1, T2, T3, T4, T5, T6) -> U
): Result<U, E> =
    r1.flatMap { v1 ->
        r2.flatMap { v2 ->
            r3.flatMap { v3 ->
                r4.flatMap { v4 ->
                    r5.flatMap { v5 ->
                        r6.map { v6 ->
                            transform(v1, v2, v3, v4, v5, v6)
                        }
                    }
                }
            }
        }
    }

