package dev.forkhandles.tx.jpa

import dev.forkhandles.tx.Transactional
import dev.forkhandles.tx.TransactorContract
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.LockModeType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

abstract class JpaTransactorContract : TransactorContract() {
    abstract val emf : EntityManagerFactory
    override lateinit var transactor: Transactional<JpaCounter>
    
    abstract val lockMode : LockModeType
    
    @BeforeEach
    fun createCounter(testInfo: TestInfo) {
        transactor = JpaTransactor(emf) { em ->
            val testName = testInfo.testMethod.map { it.name }.orElseThrow()
            JpaCounter(em, testName, lockMode)
        }
    }
    
    @Test
    fun `passes through unrecoverable database errors without retry`() {
        assertThrows<Exception> {
            transactor.perform { counter ->
                counter.causeUnrecoverableFailure()
            }
        }
    }
}
