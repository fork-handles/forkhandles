package com.natpryce

import dev.forkhandles.result4k.Failure as FailureNew
import dev.forkhandles.result4k.Result as ResultNew
import dev.forkhandles.result4k.Success as Success1
import dev.forkhandles.result4k.resultFrom as resultFromNew

@Deprecated("Repackaged", ReplaceWith("dev.forkhandles.result4k.Result<A, B>"))
typealias Result<A, B> = ResultNew<A, B>

@Deprecated("Repackaged", ReplaceWith("dev.forkhandles.result4k.Success<A>"))
typealias Success<A> = Success1<A>

@Deprecated("Repackaged", ReplaceWith("dev.forkhandles.result4k.Failure<A>"))
typealias Failure<A> = FailureNew<A>

@Deprecated("Repackaged", ReplaceWith("dev.forkhandles.result4k.resultFrom()"))
inline fun <T> resultFrom(block: () -> T): Result<T, Exception> = resultFromNew(block)

