package com.natpryce

import dev.forkhandles.result4k.asResultOr as asResultOrNew
import dev.forkhandles.result4k.failureOrNull as failureOrNullNew
import dev.forkhandles.result4k.filterNotNull as filterNotNullNew
import dev.forkhandles.result4k.valueOrNull as valueOrNullNew

@Deprecated("Repackaged", ReplaceWith("dev.forkhandles.result4k.asResultOr()"))
inline fun <T, E> T?.asResultOr(failureDescription: () -> E) = asResultOrNew(failureDescription)

@Deprecated("Repackaged", ReplaceWith("dev.forkhandles.result4k.filterNotNull()"))
inline fun <T : Any, E> Result<T?, E>.filterNotNull(failureDescription: () -> E) = filterNotNullNew(failureDescription)

@Deprecated("Repackaged", ReplaceWith("dev.forkhandles.result4k.valueOrNull()"))
fun <T, E> Result<T, E>.valueOrNull() = valueOrNullNew()

@Deprecated("Repackaged", ReplaceWith("dev.forkhandles.result4k.failureOrNull()"))
fun <T, E> Result<T, E>.failureOrNull() = failureOrNullNew()

