package dev.forkhandles.result4k

inline fun <T1, U, E> flatZip(
    r1: Result<T1, E>,
    transform: (T1) -> Result<U, E>
): Result<U, E> =
    r1.flatMap(transform)

inline fun <T1, T2, U, E> flatZip(
    r1: Result<T1, E>,
    r2: Result<T2, E>,
    transform: (T1, T2) -> Result<U, E>
): Result<U, E> =
    r1.flatMap { v1 ->
        r2.flatMap { v2 ->
            transform(v1, v2)
        }
    }

inline fun <T1, T2, T3, U, E> flatZip(
    r1: Result<T1, E>,
    r2: Result<T2, E>,
    r3: Result<T3, E>,
    transform: (T1, T2, T3) -> Result<U, E>
): Result<U, E> =
    r1.flatMap { v1 ->
        r2.flatMap { v2 ->
            r3.flatMap { v3 ->
                transform(v1, v2, v3)
            }
        }
    }

inline fun <T1, T2, T3, T4, U, E> flatZip(
    r1: Result<T1, E>,
    r2: Result<T2, E>,
    r3: Result<T3, E>,
    r4: Result<T4, E>,
    transform: (T1, T2, T3, T4) -> Result<U, E>
): Result<U, E> =
    r1.flatMap { v1 ->
        r2.flatMap { v2 ->
            r3.flatMap { v3 ->
                r4.flatMap { v4 ->
                    transform(v1, v2, v3, v4)
                }
            }
        }
    }

inline fun <T1, T2, T3, T4, T5, U, E> flatZip(
    r1: Result<T1, E>,
    r2: Result<T2, E>,
    r3: Result<T3, E>,
    r4: Result<T4, E>,
    r5: Result<T5, E>,
    transform: (T1, T2, T3, T4, T5) -> Result<U, E>
): Result<U, E> =
    r1.flatMap { v1 ->
        r2.flatMap { v2 ->
            r3.flatMap { v3 ->
                r4.flatMap { v4 ->
                    r5.flatMap { v5 ->
                        transform(v1, v2, v3, v4, v5)
                    }
                }
            }
        }
    }

inline fun <T1, T2, T3, T4, T5, T6, U, E> flatZip(
    r1: Result<T1, E>,
    r2: Result<T2, E>,
    r3: Result<T3, E>,
    r4: Result<T4, E>,
    r5: Result<T5, E>,
    r6: Result<T6, E>,
    transform: (T1, T2, T3, T4, T5, T6) -> Result<U, E>
): Result<U, E> =
    r1.flatMap { v1 ->
        r2.flatMap { v2 ->
            r3.flatMap { v3 ->
                r4.flatMap { v4 ->
                    r5.flatMap { v5 ->
                        r6.flatMap { v6 ->
                            transform(v1, v2, v3, v4, v5, v6)
                        }
                    }
                }
            }
        }
    }
