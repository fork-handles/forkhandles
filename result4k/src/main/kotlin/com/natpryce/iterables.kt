package com.natpryce

import dev.forkhandles.result4k.allValues as allValuesNew
import dev.forkhandles.result4k.anyValues as anyValuesNew
import dev.forkhandles.result4k.partition as partitionNew

@Deprecated("Repackaged", ReplaceWith("dev.forkhandles.result4k.allValues()"))
fun <T, E> Iterable<Result<T, E>>.allValues() = allValuesNew()

@Deprecated("Repackaged", ReplaceWith("dev.forkhandles.result4k.anyValues()"))
fun <T, E> Iterable<Result<T, E>>.anyValues() = anyValuesNew()

@Deprecated("Repackaged", ReplaceWith("dev.forkhandles.result4k.partition()"))
fun <T, E> Iterable<Result<T, E>>.partition() = partitionNew()
