package dev.forkhandles.tx.jdbc

import dev.forkhandles.tx.Counter
import dev.forkhandles.tx.Transactional
import org.testcontainers.containers.JdbcDatabaseContainer
import java.sql.Connection

class JdbcCounter(
    val connection: Connection,
    val name: String
) : Counter {
    fun init() {
        connection.prepareStatement(
            "INSERT INTO COUNTER (id,count) VALUES (?,0)"
        ).use { s ->
            s.setString(1, name)
            s.executeUpdate()
        }
    }
    
    override fun incrementBy(n: Int) {
        val newCount = count() + n
        
        connection.prepareStatement(
            "UPDATE COUNTER SET count = ? WHERE id = ?"
                                                                                                                                                                                                ).use { s ->
            s.setInt(1, newCount)
            s.setString(2, name)
            
            s.executeUpdate()
        }
    }
    
    override fun count(): Int {
        return connection.prepareStatement(
            "SELECT count FROM COUNTER WHERE id = ?"
        ).use { s ->
            s.setString(1, name)
            s.executeQuery().use { rs ->
                require(rs.next()) { "no counter with id $name" }
                rs.getInt("count")
            }
        }
    }
}

fun createSchema(c: Connection): Boolean = c.createStatement().use { s ->
    s.execute(
        """
        create table COUNTER (
            id VARCHAR(64) PRIMARY KEY,
            count NUMERIC(8) NOT NULL DEFAULT 0
        )
        """
    )
}

fun createCounterTransactor(
    database: JdbcDatabaseContainer<*>,
    testName: String
): Transactional<JdbcCounter> {
    val transactor = JdbcTransactor(
        createConnection = { database.createConnection("") },
        createWrapper = { JdbcCounter(it, testName) }
    )
    
    transactor.perform { it.init() }
    
    return transactor
}
