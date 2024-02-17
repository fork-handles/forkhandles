package dev.forkhandles.state4k

/**
 * Builder for the mechanics of how to transition out of a particular state
 */
class StateBuilder<State, Entity, Command>(
    val start: State,
    val transitions: List<StateTransition<State, Entity, *, Command>> = emptyList()
) {
    /**
     * Define a state and the transitions out of that state via events, with an optional command to send next
     */
    inline fun <reified Event : Any> transition(
        end: State,
        noinline applyTo: (Event, Entity) -> Entity = { _, entity -> entity },
        nextCommand: Command? = null
    ) = StateBuilder(
        start,
        transitions + StateTransition(start, Event::class, end, applyTo, nextCommand)
    )
}
