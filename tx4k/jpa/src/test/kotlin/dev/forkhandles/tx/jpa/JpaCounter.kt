package dev.forkhandles.tx.jpa

import dev.forkhandles.tx.Counter
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityManager
import jakarta.persistence.Id

@Entity

class NamedCount(
    @Id
    var id: String? = null,
    
    @Column
    var value: Int = 0
)

class JpaCounter(
    val entityManager: EntityManager,
    val name: String
) : Counter {
    fun init() {
        entityManager.persist(NamedCount(name))
    }
    
    override fun incrementBy(n: Int) {
        val count = loadCount()
        count.value += n
    }
    
    override fun count(): Int {
        return loadCount().value
    }
    
    private fun loadCount(): NamedCount =
        entityManager.find(NamedCount::class.java, name)
            ?: error("no count found for name: $name")
    
    fun causeUnrecoverableFailure() {
        entityManager.persist(NamedCount(null, 0))
    }
}
