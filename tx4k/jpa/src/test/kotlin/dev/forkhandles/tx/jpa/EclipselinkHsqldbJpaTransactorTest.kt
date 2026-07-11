package dev.forkhandles.tx.jpa

import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.LockModeType
import jakarta.persistence.PersistenceConfiguration
import jakarta.persistence.PersistenceConfiguration.JDBC_URL
import org.eclipse.persistence.config.ExclusiveConnectionMode.Always
import org.eclipse.persistence.config.PersistenceUnitProperties.CACHE_SHARED_DEFAULT
import org.eclipse.persistence.config.PersistenceUnitProperties.EXCLUSIVE_CONNECTION_IS_LAZY
import org.eclipse.persistence.config.PersistenceUnitProperties.EXCLUSIVE_CONNECTION_MODE
import org.eclipse.persistence.config.PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION
import org.eclipse.persistence.config.PersistenceUnitProperties.SESSION_CUSTOMIZER
import org.eclipse.persistence.config.PersistenceUnitProperties.TARGET_DATABASE
import org.eclipse.persistence.config.TargetDatabase.HSQL
import org.eclipse.persistence.sessions.DatabaseLogin
import org.eclipse.persistence.sessions.DatabaseLogin.TRANSACTION_SERIALIZABLE
import org.eclipse.persistence.sessions.SessionCustomizer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.eclipse.persistence.jpa.JpaEntityManagerFactory as EclipselinkEntityManagerFactory
import org.eclipse.persistence.jpa.PersistenceProvider as EclipselinkPersistenceProvider


class EclipselinkHsqldbJpaTransactorTest : JpaTransactorContract() {
    // I cannot get Eclipselink working with MVCC serializable transactions
    override val lockMode: LockModeType = LockModeType.PESSIMISTIC_WRITE
    
    override val emf: EntityManagerFactory
        get() = Companion.emf ?: error("no EntityManagerFactory")
    
    companion object {
        var emf: EntityManagerFactory? = null
        
        @BeforeAll
        @JvmStatic
        fun createEntityManagerFactory() {
            val persistenceUnitName = this::class.qualifiedName
            val provider = EclipselinkPersistenceProvider()
            
            emf = provider.createEntityManagerFactory(
                PersistenceConfiguration(persistenceUnitName)
                    .managedClass(NamedCount::class.java)
                    .property(JDBC_URL, "jdbc:hsqldb:mem:$persistenceUnitName")
                    .property(SCHEMA_GENERATION_DATABASE_ACTION, "drop-and-create")
                    .property(TARGET_DATABASE, HSQL)
                    .property(CACHE_SHARED_DEFAULT, "false")
                    .property(EXCLUSIVE_CONNECTION_MODE, Always)
                    .property(EXCLUSIVE_CONNECTION_IS_LAZY, "false")
                    .property(SESSION_CUSTOMIZER, SessionCustomizer { session ->
                        when (val login = session.datasourceLogin) {
                            is DatabaseLogin -> login.transactionIsolation = TRANSACTION_SERIALIZABLE
                            else -> error("Unsupported login type ${login::class.java}")
                        }
                    })
            )
            
            assertTrue(emf is EclipselinkEntityManagerFactory) {
                "$persistenceUnitName: should be an Eclipselink EntityManagerFactory, " +
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
