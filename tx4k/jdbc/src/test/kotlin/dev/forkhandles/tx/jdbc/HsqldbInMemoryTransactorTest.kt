@file:OptIn(ExperimentalUuidApi::class)

package dev.forkhandles.tx.jdbc

import dev.forkhandles.tx.Transactional
import org.hsqldb.jdbc.JDBCDataSource
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInfo
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer
import kotlin.uuid.ExperimentalUuidApi

class HsqldbInMemoryTransactorTest : JdbcTransactorContract() {
    override lateinit var transactor: Transactional<JdbcCounter>
    
    @BeforeEach
    fun createCounter(testInfo: TestInfo) {
        val testName = testInfo.testMethod.map { it.name }.orElseThrow()
        transactor = JdbcTransactor(
            createConnection = { dataSource.connection },
            createWrapper = { JdbcCounter(it, testName) }
        )
        
        transactor.perform { it.init() }
    }
    
    companion object {
        val dataSource = JDBCDataSource().apply {
            database = "jdbc:hsqldb:mem:${HsqldbInMemoryTransactorTest::class.simpleName}"
        }
        
        @BeforeAll
        @JvmStatic
        fun createSchema() {
            dataSource.connection.use(::createSchema)
        }
        
        @AfterAll
        @JvmStatic
        fun closeDataSource() {
            dataSource.connection.use { c ->
                c.createStatement().use { s ->
                    s.execute("SHUTDOWN")
                }
            }
        }
    }
}

