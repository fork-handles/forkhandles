package dev.forkhandles.state4k

import kotlin.reflect.KClass

data class StateTransition<State, Entity, Event : Any, Command>(
    val start: State,
    val event: KClass<Event>,
    val end: State,
    val applyTo: (Event, Entity) -> Entity,
    val nextCommand: Command?
)
