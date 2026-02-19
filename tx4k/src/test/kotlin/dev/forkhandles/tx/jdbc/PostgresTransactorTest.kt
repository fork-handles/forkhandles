@file:OptIn(ExperimentalUuidApi::class)

package dev.forkhandles.tx.jdbc

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
    override lateinit var transactor: Transactional<JdbcCounter>
    
    @BeforeEach
    fun createCounter(testInfo: TestInfo) {
        transactor = JdbcTransactor(
            createConnection = { database.createConnection("") },
            createWrapper = { JdbcCounter(it, testInfo.displayName) }
        )
        
        transactor.perform { it.init() }
    }
    
    companion object {
        @Container
        @JvmStatic
        private val database = PostgreSQLContainer("postgres:18.2")
        
        @BeforeAll
        @JvmStatic
        fun createSchema() {
            database.start()
            database.createConnection("")
                .use(::createSchema)
        }
        
        @AfterAll
        @JvmStatic
        fun cleanUp() {
            database.stop()
        }
    }
}

