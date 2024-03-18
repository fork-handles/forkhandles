package dev.forkhandles.state4k

/**
 * The various outcomes which can happen out of a state machine transition
 */
sealed interface StateTransitionResult<StateId, Entity, CommandType> {
    val entity: Entity

    /**
     * Apply a transformation to update the entity inside the result. Useful for
     * manipulating the result.
     */
    fun map(fn: (Entity) -> Entity): StateTransitionResult<StateId, Entity, CommandType>

    /**
     * This transition was valid and the updated entity is enclosed in the field
     */
    data class OK<EntityState, Entity, CommandType>(override val entity: Entity) :
        StateTransitionResult<EntityState, Entity, CommandType> {
        override fun map(fn: (Entity) -> Entity) = copy(entity = fn(entity))
    }

    /**
     * This transition via an event was not valid. The unmodified entity is enclosed in the field.
     */
    data class IllegalEvent<EntityState, Entity, CommandType>(
        override val entity: Entity,
        val event: Any
    ) : StateTransitionResult<EntityState, Entity, CommandType> {
        override fun map(fn: (Entity) -> Entity) = copy(entity = fn(entity))
    }

    /**
     * This transition via an command was not valid. The unmodified entity is enclosed in the field.
     */
    data class IllegalCommand<State, Entity, CommandType>(
        override val entity: Entity,
        val commandType: CommandType
    ) : StateTransitionResult<State, Entity, CommandType> {
        override fun map(fn: (Entity) -> Entity) = copy(entity = fn(entity))
    }
}
