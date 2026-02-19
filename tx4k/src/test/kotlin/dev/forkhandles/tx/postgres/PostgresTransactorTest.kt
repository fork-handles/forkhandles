@file:OptIn(ExperimentalUuidApi::class)

package dev.forkhandles.tx.postgres

import dev.forkhandles.tx.Transactional
import dev.forkhandles.tx.TransactorContract
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInfo
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer
import kotlin.uuid.ExperimentalUuidApi

// language = postgresql
@Testcontainers
class PostgresTransactorTest : TransactorContract() {
    override lateinit var transactor: Transactional<PostgresCounter>
    
    @BeforeEach
    fun createCounter(testInfo: TestInfo) {
        transactor = PostgresTransactor(
            createConnection = { postgres.createConnection("") },
            createWrapper = { PostgresCounter(it, testInfo.displayName) }
        )
        
        transactor.perform { it.init() }
    }
    
    companion object {
        @Container
        @JvmStatic
        private val postgres = PostgreSQLContainer("postgres:18.2")
        
        @BeforeAll
        @JvmStatic
        fun createSchema() {
            postgres.start()
            
            postgres.createConnection("").use { c ->
                c.createStatement().use { s ->
                    s.execute(
                        """
                        create table COUNTER (
                            id TEXT PRIMARY KEY,
                            count INT NOT NULL DEFAULT 0
                        )
                        """
                    )
                }
            }
        }
        
        @AfterAll
        @JvmStatic
        fun cleanUp() {
            postgres.stop()
        }
    }
}
