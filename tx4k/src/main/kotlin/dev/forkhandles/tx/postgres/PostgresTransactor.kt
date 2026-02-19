package dev.forkhandles.tx.postgres

import dev.forkhandles.tx.Transactor
import java.sql.Connection
import java.sql.SQLException


class PostgresTransactor<out API>(
    private val createConnection: () -> Connection,
    private val createWrapper: (Connection) -> API
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
}
