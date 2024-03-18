package dev.forkhandles.state4k

/**
 * Responsible for retrieving and setting the state on an entity
 */
interface StateIdLens<Entity, StateId> {
    operator fun invoke(entity: Entity): StateId
    operator fun invoke(entity: Entity, newState: StateId): Entity

    companion object {
        /**
         * Convenience function for creating an EntityStateLens
         */
        operator fun <Entity, StateId> invoke(
            get: (Entity) -> StateId,
            set: (Entity, StateId) -> Entity,
        ) = object : StateIdLens<Entity, StateId> {
            override fun invoke(entity: Entity) = get(entity)
            override fun invoke(entity: Entity, newState: StateId) = set(entity, newState)
        }
    }
}
