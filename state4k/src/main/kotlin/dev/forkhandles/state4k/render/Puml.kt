package dev.forkhandles.state4k.render

import dev.forkhandles.state4k.StateMachineRenderer

/**
 * Standard PUML diagram generator for a StateMachine
 */
fun Puml(title: String, commandColour: String = "PaleGoldenRod") = StateMachineRenderer { states ->
    ""
//    val commands = transitions
//        .groupBy { it.end.toString() }
//        .mapValues { it.value.mapNotNull { it.nextCommand?.toString() } }
//        .map {
//            val stateName = it.key
//            """
//            |state $stateName {
//            |${
//                it.value.sortedBy { it }
//                    .joinToString("\n") { command -> "    state \"$command\" as ${stateName}_$command <<Command>>" }
//            }
//            |}
//            """.trimMargin()
//        }.joinToString("\n")
//
//    """
//    |@startuml
//    |skinparam state  {
//    |   BackgroundColor<<Command>> $commandColour
//    |   BorderColor<<Command>> $commandColour
//    |}
//    |
//    |$commands
//    |
//    |title $title
//    |${
//        transitions
//            .joinToString("\n") { "  ${it.start} --> ${it.end} : ${it.event.simpleName}" }
//    }
//    |@enduml""".trimMargin()
}
