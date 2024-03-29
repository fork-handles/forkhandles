package dev.forkhandles.state4k.render

import com.oneeyedmen.okeydoke.Approver
import com.oneeyedmen.okeydoke.junit5.ApprovalsExtension
import dev.forkhandles.state4k.teaStateMachine
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(ApprovalsExtension::class)
class PumlRendererTest {

    @Test
    fun asPuml(approver: Approver) {
        approver.assertApproved(teaStateMachine.renderUsing(Puml("helloworld")))
    }
}
