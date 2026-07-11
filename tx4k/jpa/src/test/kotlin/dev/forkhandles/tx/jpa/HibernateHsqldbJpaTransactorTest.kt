package dev.forkhandles.tx.jpa

import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.LockModeType
import jakarta.persistence.PersistenceConfiguration
import jakarta.persistence.PersistenceConfiguration.JDBC_URL
import jakarta.persistence.PersistenceConfiguration.SCHEMAGEN_DATABASE_ACTION
import org.hibernate.SessionFactory
import org.hibernate.cfg.JdbcSettings.AUTOCOMMIT
import org.hibernate.cfg.JdbcSettings.CONNECTION_HANDLING
import org.hibernate.cfg.JdbcSettings.DIALECT
import org.hibernate.cfg.JdbcSettings.ISOLATION
import org.hibernate.dialect.HSQLDialect
import org.hibernate.jpa.HibernatePersistenceProvider
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode.IMMEDIATE_ACQUISITION_AND_HOLD
import org.hibernate.tool.schema.Action.SPEC_ACTION_DROP_AND_CREATE
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import java.sql.Connection.TRANSACTION_SERIALIZABLE


class HibernateHsqldbJpaTransactorTest : JpaTransactorContract() {
    // Hibernate works with MVCC serializable transactions
    override val lockMode: LockModeType = LockModeType.NONE
    
    override val emf: EntityManagerFactory
        get() = Companion.emf ?: error("no EntityManagerFactory")
    
    companion object {
        var emf: EntityManagerFactory? = null
        
        @BeforeAll
        @JvmStatic
        fun createSchema() {
            val persistenceUnitName = this::class.qualifiedName
            val provider = HibernatePersistenceProvider()
            
            emf = provider.createEntityManagerFactory(
                PersistenceConfiguration(persistenceUnitName)
                    .managedClass(NamedCount::class.java)
                    .property(JDBC_URL, "jdbc:hsqldb:mem:$persistenceUnitName")
                    .property(SCHEMAGEN_DATABASE_ACTION, SPEC_ACTION_DROP_AND_CREATE)
                    .property(DIALECT, HSQLDialect())
                    .property(CONNECTION_HANDLING, IMMEDIATE_ACQUISITION_AND_HOLD)
                    .property(AUTOCOMMIT, false)
                    .property(ISOLATION, TRANSACTION_SERIALIZABLE)
            )
            
            assertTrue(emf is SessionFactory) {
                "$persistenceUnitName: should be a Hibernate EntityManagerFactory, " +
                    "but was ${emf?.javaClass}"
            }
        }
        
        @AfterAll
        @JvmStatic
        fun cleanUp() {
            emf?.close()
        }
    }
}

