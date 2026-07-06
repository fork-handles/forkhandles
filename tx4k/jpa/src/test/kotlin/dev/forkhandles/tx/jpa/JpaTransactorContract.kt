package dev.forkhandles.tx.jpa

import dev.forkhandles.tx.Transactional
import dev.forkhandles.tx.TransactorContract
import org.junit.jupiter.api.assertThrows
import java.sql.SQLException
import kotlin.test.Test

abstract class JpaTransactorContract : TransactorContract() {
    abstract override val transactor: Transactional<JpaCounter>
    
    @Test
    fun `passes through unrecoverable database errors without retry`() {
        assertThrows<Exception> {
            transactor.perform { counter ->
                counter.causeUnrecoverableFailure()
            }
        }
    }
}
