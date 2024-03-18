package dev.forkhandles.state4k

/**
 * Builder for the mechanics of how to transition out of a particular state
 */
class StateBuilder<State, Entity, Command>(
    val start: State,
    val onEnter: Command? = null,
    val transitions: List<StateTransition<State, Entity, *>> = emptyList()
) {
    fun onEnter(nextCommand: Command) = StateBuilder(start, nextCommand, transitions)

    /**
     * Define a state and the transitions out of that state via events, with an optional command to send next
     */
    inline fun <reified Event : Any> transition(
        end: State,
        noinline applyTo: (Event, Entity) -> Entity = { _, entity -> entity }
    ) = StateBuilder(
        start,
        onEnter,
        transitions + StateTransition(Event::class, end, applyTo)
    )
}
