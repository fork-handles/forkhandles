package dev.forkhandles.tx.jdbc

import dev.forkhandles.tx.RetryPolicy
import dev.forkhandles.tx.Transactor
import dev.forkhandles.tx.increasingBackoff
import dev.forkhandles.tx.maxAttempts
import java.sql.Connection
import java.sql.SQLException
import java.time.Duration


class JdbcTransactor<out API>(
    private val createConnection: () -> Connection,
    private val createWrapper: (Connection) -> API,
    private val retryPolicy: RetryPolicy =
        increasingBackoff(Duration.ofMillis(50)).maxAttempts(5)
) : Transactor<Connection, API>() {
    override fun createResource(): Connection = createConnection()
    
    override fun configureResource(resource: Connection) {
        resource.autoCommit = false
        resource.transactionIsolation = Connection.TRANSACTION_SERIALIZABLE
    }
    
    override fun destroyResource(resource: Connection) = resource.close()
    
    override fun createApi(resource: Connection): API =
        createWrapper(resource)
    
    override fun startTransaction(resource: Connection) {
        // Nothing required for JDBC
    }
    
    override fun rollbackTransaction(resource: Connection) = resource.rollback()
    override fun commitTransaction(resource: Connection) = resource.commit()
    
    override fun canRetry(e: Exception): Boolean =
        when (e) {
            is SQLException -> e.sqlState == "40001" || e.sqlState == "40P01"
            else -> false
        }
    
    override fun retryBackoff(attempt: Int): Duration? =
        retryPolicy(attempt)
}
