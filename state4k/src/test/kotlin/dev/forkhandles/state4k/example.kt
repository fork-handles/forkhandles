package dev.forkhandles.state4k

import MyCommandType
import MyCommandType.eject
import MyCommandType.firedOnThree
import MyCommandType.firedOnTwo
import MyEntity
import MyEvent
import MyState
import MyState.five
import MyState.four
import MyState.one
import MyState.six
import MyState.three
import MyState.two
import OneToFourEvent
import OneToSixEvent
import OneToTwoEvent
import ThreeToFourEvent
import TwoToThreeEvent
import dev.forkhandles.result4k.Success

val exampleStateMachine = StateMachine<MyState, MyEntity, MyEvent, MyCommandType, String>(
    { _, _ -> Success(Unit) },
    EntityStateLens(MyEntity::state) { entity, state -> entity.copy(state = state) },
    buildState(one)
        .transition<OneToTwoEvent>(two, { _, o -> o.copy(data = OneToTwoEvent.data) }, firedOnTwo)
        .transition<OneToFourEvent>(four, { _, o -> o.copy(data = OneToFourEvent.data) })
        .transition<OneToSixEvent>(six, { _, o -> o.copy(data = OneToSixEvent.data) }, eject),
    buildState(two)
        .transition<TwoToThreeEvent>(
            three,
            { _, o -> o.copy(data = TwoToThreeEvent.data) },
            firedOnThree
        ),
    buildState(three)
        .transition<ThreeToFourEvent>(
            four,
            { _, o -> o.copy(data = ThreeToFourEvent.data) },
            eject
        ),
    buildState(four)
        .transition<ThreeToFourEvent>(five, { _, o -> o.copy(data = ThreeToFourEvent.data) })
)

fun buildState(start: MyState) = StateBuilder<MyState, MyEntity, MyCommandType>(start)
