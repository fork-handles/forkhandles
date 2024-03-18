package dev.forkhandles.state4k

fun interface StateMachineRenderer {
    operator fun invoke(
        transitions: List<MachineState<*, *, *>>): String
}
