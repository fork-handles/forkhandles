package dev.forkhandles.tx.jdbc

import dev.forkhandles.tx.Transactional
import dev.forkhandles.tx.TransactorContract
import org.junit.jupiter.api.assertThrows
import java.sql.SQLException
import kotlin.test.Test

abstract class JdbcTransactorContract : TransactorContract() {
    abstract override val transactor: Transactional<JdbcCounter>
    
    @Test
    fun `passes through unrecoverable database errors without retry`() {
        assertThrows<SQLException> {
            transactor.perform { counter ->
                counter.causeUnrecoverableFailure()
            }
        }
    }
}
