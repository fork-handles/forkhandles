@file:OptIn(ExperimentalUuidApi::class)

package dev.forkhandles.tx.jdbc

import dev.forkhandles.tx.Counter
import dev.forkhandles.tx.Transactional
import dev.forkhandles.tx.TransactorContract
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInfo
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.mariadb.MariaDBContainer
import kotlin.uuid.ExperimentalUuidApi

// language = mariadb
@Testcontainers
class MariaDBTransactorTest : TransactorContract() {
    override lateinit var transactor: Transactional<Counter>
    
    @BeforeEach
    fun createCounter(testInfo: TestInfo) {
        transactor = createCounterTransactor(database, testInfo.displayName)
    }
    
    companion object {
        @Container
        @JvmStatic
        private val database: JdbcDatabaseContainer<*> = MariaDBContainer("mariadb:10.5.5")
        
        @BeforeAll
        @JvmStatic
        fun createSchema() {
            database.createConnection("").use(::createSchema)
        }
    }
}


