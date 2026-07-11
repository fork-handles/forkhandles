package dev.forkhandles.tx.jpa

import dev.forkhandles.tx.Counter
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityManager
import jakarta.persistence.Id
import jakarta.persistence.LockModeType

@Entity
class NamedCount(
    @Id
    var id: String? = null,
    
    @Column
    var value: Int = 0
)

class JpaCounter(
    val entityManager: EntityManager,
    val name: String,
    val lockMode : LockModeType = LockModeType.PESSIMISTIC_WRITE
) : Counter {
    override fun incrementBy(n: Int) {
        val count = loadCount()
        count.value += n
    }
    
    override fun count(): Int {
        return loadCount().value
    }
    
    private fun loadCount(): NamedCount {
        return entityManager.find(NamedCount::class.java, name, lockMode)
            ?: NamedCount(name).also { entityManager.persist(it) }
    }
    
    fun causeUnrecoverableFailure() {
        entityManager.persist(NamedCount(null, 0))
    }
}
