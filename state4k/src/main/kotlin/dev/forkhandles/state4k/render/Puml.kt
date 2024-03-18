package dev.forkhandles.state4k.render

import dev.forkhandles.state4k.StateMachineRenderer

/**
 * Standard PUML diagram generator for a StateMachine
 */
fun Puml(title: String, commandColour: String = "PaleGoldenRod") = StateMachineRenderer { states ->

    val stateDefinitions = states.joinToString("\n") {
        val stateId = it.id
        """
            |state $stateId {
            |${
            it.onEnter?.let { "    state \"$it\" as ${stateId}_$it <<Command>>" } ?: ""
        }
            |}
            """.trimMargin()
    }


    """
    |@startuml
    |skinparam state  {
    |   BackgroundColor<<Command>> $commandColour
    |   BorderColor<<Command>> $commandColour
    |}
    |
    |$stateDefinitions
    |
    |title $title
    |${
        states.joinToString("\n") { state ->
            state.transitions.joinToString("\n") { "  ${state.id} --> ${it.end} : ${it.event.simpleName}" }
        }
    }
    |@enduml""".trimMargin()
}
