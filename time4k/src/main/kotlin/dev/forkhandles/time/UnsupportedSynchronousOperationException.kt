package dev.forkhandles.time

/**
 * Thrown to report that a [DeterministicScheduler] has been asked to perform
 * a blocking wait, which is not supported.
 *
 * @author nat
 */
class UnsupportedSynchronousOperationException(message: String) :
    UnsupportedOperationException(message)