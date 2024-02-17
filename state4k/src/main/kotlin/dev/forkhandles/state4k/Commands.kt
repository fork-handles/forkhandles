package dev.forkhandles.state4k

import dev.forkhandles.result4k.Result4k

/**
 * Receiver for issuing a new command as a result of a transition
 */
fun interface Commands<Entity, Command, Error> {
    operator fun invoke(entity: Entity, command: Command): Result4k<Unit, Error>
}
