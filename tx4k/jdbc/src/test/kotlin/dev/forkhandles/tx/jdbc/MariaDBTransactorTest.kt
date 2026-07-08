@file:OptIn(ExperimentalUuidApi::class)

package dev.forkhandles.tx.jdbc

import dev.forkhandles.tx.Transactional
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
class MariaDBTransactorTest : JdbcTransactorContract() {
    override lateinit var transactor: Transactional<JdbcCounter>
    
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


