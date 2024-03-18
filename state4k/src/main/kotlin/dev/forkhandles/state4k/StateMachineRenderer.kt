package dev.forkhandles.state4k

fun interface StateMachineRenderer {
    operator fun invoke(states: List<State<*, *, *>>): String
}
