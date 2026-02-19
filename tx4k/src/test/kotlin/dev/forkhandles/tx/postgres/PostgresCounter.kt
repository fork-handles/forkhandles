package dev.forkhandles.tx.postgres

import dev.forkhandles.tx.Counter
import java.sql.Connection

class PostgresCounter(
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
        connection.prepareStatement(
            "UPDATE COUNTER SET count = count + ? WHERE id = ?"
                                                                                                                                                                                                ).use { s ->
            s.setInt(1, n)
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
