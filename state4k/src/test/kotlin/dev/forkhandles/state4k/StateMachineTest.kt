import MyCommandType.firedOnThree
import MyCommandType.firedOnTwo
import MyState.four
import MyState.one
import MyState.three
import MyState.two
import com.oneeyedmen.okeydoke.junit5.ApprovalsExtension
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.failureOrNull
import dev.forkhandles.result4k.valueOrNull
import dev.forkhandles.state4k.StateIdLens
import dev.forkhandles.state4k.StateMachine
import dev.forkhandles.state4k.StateTransitionResult
import dev.forkhandles.state4k.StateTransitionResult.IllegalCommand
import dev.forkhandles.state4k.StateTransitionResult.IllegalEvent
import dev.forkhandles.state4k.StateTransitionResult.OK
import dev.forkhandles.state4k.buildState
import dev.forkhandles.state4k.exampleStateMachine
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@ExtendWith(ApprovalsExtension::class)
class StateMachineTest {

    @Test
    fun `transitions through state machine`() {
        val stateOne = MyEntity(one, 1.0)

        expectCannotProcessCommand(stateOne, firedOnTwo)
        expectCannotProcessCommand(stateOne, firedOnThree)

        val stateTwo = exampleStateMachine.transition(stateOne, OneToTwoEvent).asEntity()
        expectThat(stateTwo).isEqualTo(MyEntity(two, 1.2))

        expectCanProcessCommand(stateTwo, firedOnTwo, TwoToThreeEvent, three)
        expectCannotProcessCommand(stateTwo, firedOnThree)

        val stateThree = exampleStateMachine.transition(stateTwo, TwoToThreeEvent).asEntity()
        expectThat(stateThree).isEqualTo(MyEntity(three, 2.3))

        expectCannotProcessCommand(stateThree, firedOnTwo)
        expectCanProcessCommand(stateThree, firedOnThree, ThreeToFourEvent, four)

        val stateFour = exampleStateMachine.transition(stateThree, ThreeToFourEvent).asEntity()
        expectThat(stateFour).isEqualTo(MyEntity(four, 3.4))

        expectCannotProcessCommand(stateFour, firedOnTwo)
        expectCannotProcessCommand(stateFour, firedOnThree)

        val stateFourViaShortcut = exampleStateMachine.transition(stateOne, OneToFourEvent).asEntity()
        expectThat(stateFourViaShortcut).isEqualTo(MyEntity(four, 1.4))

        expectCannotProcessCommand(stateFourViaShortcut, firedOnTwo)
        expectCannotProcessCommand(stateFourViaShortcut, firedOnThree)
    }

    private fun expectCanProcessCommand(
        entity: MyEntity,
        commandType: MyCommandType,
        value: MyEvent,
        newState: MyState,
    ) {
        expectThat(exampleStateMachine.transition(entity, commandType) { Success(value) }).isEqualTo(
            Success(
                OK(entity.copy(data = value.data, state = newState))
            )
        )
    }

    private fun expectCannotProcessCommand(entity: MyEntity, commandType: MyCommandType) {
        expectThat(exampleStateMachine.transition(entity, commandType) { Success(OneToTwoEvent) }).isEqualTo(
            Success(IllegalCommand(entity, commandType))
        )
    }

    private fun Result4k<StateTransitionResult<MyState, MyEntity, MyCommandType>, String>.asEntity(): MyEntity =
        (valueOrNull()!! as OK).entity

    @Test
    fun `failure during sending of next command`() {
        val stateMachine = StateMachine<MyState, MyEntity, MyEvent, MyCommandType, String>(
            { _, _ -> Failure("foo") },
            StateIdLens(MyEntity::state, MyEntity::withState),
            buildState(one)
                .transition<OneToTwoEvent>(two) { e, o -> o.copy(data = e.data) },
            buildState(two)
                .onEnter(firedOnTwo)
        )

        val transition = stateMachine.transition(MyEntity(one, 0.0), OneToTwoEvent)
        expectThat(transition.failureOrNull())
            .isEqualTo(("foo"))
    }

    @Test
    fun `failure during command processing`() {
        expectThat(exampleStateMachine.transition(MyEntity(two, 1.0), firedOnTwo) {
            Failure("foo")
        })
            .isEqualTo(Failure("foo"))
    }

    @Test
    fun `illegal transition via event`() {
        val first = MyEntity(one, 0.0)

        expectThat(exampleStateMachine.transition(first, TwoToThreeEvent).valueOrNull())
            .isEqualTo(IllegalEvent(first, TwoToThreeEvent))
    }

}

data class MyEntity(val state: MyState, val data: Double) {
    fun withState(new: MyState) = copy(state = new)
}

enum class MyState {
    one, two, three, four, five, six
}


enum class MyCommandType {
    firedOnTwo, firedOnThree, eject
}

interface MyEvent {
    val data: Double
}

data object OneToTwoEvent : MyEvent {
    override val data = 1.2
}

data object TwoToThreeEvent : MyEvent {
    override val data = 2.3
}

data object OneToFourEvent : MyEvent {
    override val data = 1.4
}

data object ThreeToFourEvent : MyEvent {
    override val data = 3.4
}

data object OneToSixEvent : MyEvent {
    override val data = 1.6
}
