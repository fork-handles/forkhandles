package dev.forkhandles.state4k

import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.valueOrNull
import dev.forkhandles.state4k.TeaCommands.DoYouHaveMilk
import dev.forkhandles.state4k.TeaEvent.MilkIsEmpty
import dev.forkhandles.state4k.TeaEvent.MilkIsFull
import dev.forkhandles.state4k.TeaEvent.MilkPlease
import dev.forkhandles.state4k.TeaEvent.NoMilkPlease
import dev.forkhandles.state4k.TeaEvent.PourWater
import dev.forkhandles.state4k.TeaEvent.TurnOnKettle
import dev.forkhandles.state4k.TeaState.BlackTea
import dev.forkhandles.state4k.TeaState.BoilingWater
import dev.forkhandles.state4k.TeaState.CheckForMilk
import dev.forkhandles.state4k.TeaState.GetCup
import dev.forkhandles.state4k.TeaState.SteepingTea
import dev.forkhandles.state4k.TeaState.WhiteTea
import dev.forkhandles.state4k.render.Puml
import kotlin.random.Random

// this is our entity - it tracks the state
data class CupOfTea(val state: TeaState, val lastAction: String)

// the various states the entity can be in
enum class TeaState {
    GetCup, BoilingWater, SteepingTea, CheckForMilk, WhiteTea, BlackTea
}

// commands define actions which can result in dynamically generated events
enum class TeaCommands {
    DoYouHaveMilk
}

// events transition the machine from one state to another
sealed interface TeaEvent {
    data object TurnOnKettle : TeaEvent
    data object PourWater : TeaEvent
    data object MilkPlease : TeaEvent
    data object NoMilkPlease : TeaEvent
    data object MilkIsFull : TeaEvent
    data object MilkIsEmpty : TeaEvent
}

// the lens gets and sets the state on the Entity
val lens = StateIdLens(CupOfTea::state) { entity, state -> entity.copy(state = state) }

// commands is responsible for issuing new orders which will generate new events
val commands = { entity: CupOfTea, command: TeaCommands ->
    println("Issuing command $command for $entity")
    Success(Unit)
}

// define the machine
val teaStateMachine = StateMachine<TeaState, CupOfTea, TeaEvent, TeaCommands, String>(
    commands,
    lens,

    // the state transitions for GetCup - we don't need to update the entity
    StateBuilder<TeaState, CupOfTea, TeaCommands>(GetCup)
        .transition<TurnOnKettle>(BoilingWater),

    // the state transitions for BoilingWater - we can update the entity
    StateBuilder<TeaState, CupOfTea, TeaCommands>(BoilingWater)
        .transition<PourWater>(SteepingTea) { event: PourWater, entity: CupOfTea ->
            entity.copy(lastAction = "Waiting...")
        },

    // when we enter SteepingTea, we ask if they have milk (a command). The result of that
    // command will be a MilkPlease or NoMilkPlease event
    StateBuilder<TeaState, CupOfTea, TeaCommands>(SteepingTea)
        .onEnter(DoYouHaveMilk)
        .transition<MilkPlease>(CheckForMilk)
        .transition<NoMilkPlease>(BlackTea),

    StateBuilder<TeaState, CupOfTea, TeaCommands>(CheckForMilk)
        .transition<MilkIsFull>(WhiteTea)
        .transition<MilkIsEmpty>(BlackTea),

    StateBuilder(BlackTea)
)

// this is the type of the result of a transition
typealias TeaResult = Result<StateTransitionResult<TeaState, CupOfTea, TeaCommands>, String>

fun main() {
    // returns OK with the updated entity - state only,
    val boilingKettle: TeaResult = teaStateMachine.transition(
        CupOfTea(GetCup, "-"),
        TurnOnKettle
    )

    val updatedCupOfTea = boilingKettle.valueOrNull()!!.entity
    println(updatedCupOfTea)

    // returns OK with the updated entity - the lastAction is updated
    val steepingTea: TeaResult = teaStateMachine.transition(
        updatedCupOfTea,
        PourWater
    )

    val updatedCupOfTea2 = steepingTea.valueOrNull()!!.entity
    println(updatedCupOfTea2)

    // returns OK with the updated entity in state three or four
    val blackOrCheckingForMilk: TeaResult = teaStateMachine.transition(updatedCupOfTea2, DoYouHaveMilk) {
        // imagine a remote operation here which could go one of 2 ways (or fail!)
        when (Random.nextBoolean()) {
            true -> Success(NoMilkPlease)
            false -> Success(MilkPlease)
        }
    }

    println(blackOrCheckingForMilk)

    // we can display the state machine as a PlantUML diagram
    println(teaStateMachine.renderUsing(Puml("simple")))
}

