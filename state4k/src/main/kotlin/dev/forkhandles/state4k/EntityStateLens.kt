package dev.forkhandles.state4k

/**
 * Responsible for retrieving and setting the state on an entity
 */
interface EntityStateLens<Entity, State> {
    operator fun invoke(entity: Entity): State
    operator fun invoke(entity: Entity, newState: State): Entity

    companion object {
        /**
         * Convenience function for creating an EntityStateLens
         */
        operator fun <Entity, State> invoke(
            get: (Entity) -> State,
            set: (Entity, State) -> Entity,
        ) = object : EntityStateLens<Entity, State> {
            override fun invoke(entity: Entity) = get(entity)
            override fun invoke(entity: Entity, newState: State) = set(entity, newState)
        }
    }
}
