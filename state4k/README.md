# State4k

<a href="https://mvnrepository.com/artifact/dev.forkhandles"><img alt="Download" src="https://img.shields.io/maven-central/v/dev.forkhandles/forkhandles-bom"></a>
[![.github/workflows/build.yaml](https://github.com/fork-handles/forkhandles/actions/workflows/build.yaml/badge.svg)](https://github.com/fork-handles/forkhandles/actions/workflows/build.yaml)

<a href="http//www.apache.org/licenses/LICENSE-2.0"><img alt="GitHub license" src="https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat"></a>
<a href="https://codebeat.co/projects/github-com-fork-handles-forkhandles-trunk"><img alt="codebeat badge" src="https://codebeat.co/badges/5b369ed4-af27-46f4-ad9c-a307d900617e"></a>

Simple state machine modelling. Define the state-machine in terms of states, events and commands

## Installation

In Gradle, install the ForkHandles BOM and then this module in the dependency block:

```kotlin
implementation(platform("dev.forkhandles:forkhandles-bom:X.Y.Z"))
implementation("dev.forkhandles:state4k")
```

## How to use

State4k introduces a mechanic for moving a state machine from state to state using events in a strict fashion. To define a state machine, you need:

1. An `Entity` which is the object being modelled
2. A set of `States`, which the entity can be in
3. A set of `Events` to transition between those states. Each transition may result in an optional `Command` being generated as a reaction to the transition. 

The model is built by creating the machine with a set of transitions which tie a starting `State`, an `Event`, a modification process to the `Entity` when that event is received, and an optional `Command` to generate and send upon the transition.

Transitions occur in one of 2 ways:

1. Receive a known out-of-band `Event`. This modifies the state of the entity in a known way, and may generate a `Command` as per the defined transition table.
2. Receive and process a `Command`, which will result in one of a discreet set of `Events` to be applied (see #1)

## Example

We have a state machine which has 4 states. On transition to state 2 we generate a command, the result of which will generate one of 2 events:

<img src="example.png" alt="state machine"/>

The entities look like:
```kotlin
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
```

We can define the state machine in code as:

```kotlin
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
```

To manipulate the machine, we can call one of 2 methods - one for async events and one for command processing (which will result in a discreet event being generated). Each transition results in a `Result4k` result determining if the transition was successful

```kotlin
// this is the type of the result of a transition
typealias TeaResult = Result<StateTransitionResult<TeaState, CupOfTea, TeaCommands>, String>

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

```

Note that the storage of the controlled entity is done entirely outside of State4k. The typical model is for commands to be issued to a queue and the reprocessed back into the machine. In the case of a database, you will want to process each command or async event in an "select for update"-type block to ensure that only a single operation is processed at once.
