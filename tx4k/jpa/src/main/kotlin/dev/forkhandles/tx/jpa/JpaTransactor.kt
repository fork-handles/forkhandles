package dev.forkhandles.tx.jpa

import dev.forkhandles.tx.RetryPolicy
import dev.forkhandles.tx.Transactor
import dev.forkhandles.tx.increasingBackoff
import dev.forkhandles.tx.jdbc.jdbcStandardRetryability
import dev.forkhandles.tx.maxAttempts
import dev.forkhandles.tx.withAdditiveJitter
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.RollbackException
import java.time.Duration


class JpaTransactor<out API>(
    private val emf: EntityManagerFactory,
    private val retryPolicy: RetryPolicy =
        increasingBackoff(Duration.ofMillis(50))
            .withAdditiveJitter()
            .maxAttempts(5),
    private val retryableFailurePolicy: (RollbackException) -> Boolean =
        ::jpaDefaultRetryability,
    private val createWrapper: (EntityManager) -> API

) : Transactor<EntityManager, API>() {
    override fun createResource(): EntityManager =
        emf.createEntityManager()
    
    override fun configureResource(resource: EntityManager) {
    }
    
    override fun destroyResource(resource: EntityManager) =
        resource.close()
    
    override fun createApi(resource: EntityManager): API =
        createWrapper(resource)
    
    override fun startTransaction(resource: EntityManager) {
        resource.transaction.begin()
    }
    
    override fun rollbackTransaction(resource: EntityManager) {
        resource.transaction.rollback()
    }
    
    override fun commitTransaction(resource: EntityManager) {
        resource.transaction.commit()
    }
    
    override fun canRetry(e: Exception): Boolean =
        e is RollbackException && retryableFailurePolicy(e)
    
    override fun retryBackoff(attempt: Int): Duration? =
        retryPolicy(attempt)
}

fun jpaDefaultRetryability(e: RollbackException): Boolean =
    e.causes().any(::jdbcStandardRetryability)

private fun Throwable.causes(): Sequence<Throwable> =
    generateSequence(this) { it.cause }
