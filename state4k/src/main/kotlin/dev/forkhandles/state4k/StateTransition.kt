package dev.forkhandles.state4k

import kotlin.reflect.KClass

data class StateTransition<StateId, Entity, Event : Any>(
    val event: KClass<Event>,
    val end: StateId,
    val applyTo: (Event, Entity) -> Entity
)
