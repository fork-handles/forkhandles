package dev.forkhandles.tx.jpa

import dev.forkhandles.tx.Transactional
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.PersistenceConfiguration
import jakarta.persistence.PersistenceConfiguration.JDBC_URL
import jakarta.persistence.PersistenceConfiguration.SCHEMAGEN_DATABASE_ACTION
import org.hibernate.cfg.JdbcSettings.AUTOCOMMIT
import org.hibernate.cfg.JdbcSettings.CONNECTION_HANDLING
import org.hibernate.cfg.JdbcSettings.DIALECT
import org.hibernate.cfg.JdbcSettings.ISOLATION
import org.hibernate.dialect.HSQLDialect
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode.IMMEDIATE_ACQUISITION_AND_HOLD
import org.hibernate.tool.schema.Action.SPEC_ACTION_DROP_AND_CREATE
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInfo
import java.sql.Connection.TRANSACTION_SERIALIZABLE


class HibernateHsqldbJpaTransactorTest : JpaTransactorContract() {
    override lateinit var transactor: Transactional<JpaCounter>
    
    @BeforeEach
    fun createCounter(testInfo: TestInfo) {
        transactor = JpaTransactor(emf) { em ->
            JpaCounter(em, testInfo.testMethod.map { it.name }.orElseThrow())
        }
        
        transactor.perform { it.init() }
    }
    
    companion object {
        val persistenceUnitName = HibernateHsqldbJpaTransactorTest::class.simpleName
        
        val persistenceConfiguration: PersistenceConfiguration =
            PersistenceConfiguration(persistenceUnitName)
                .managedClass(NamedCount::class.java)
                .property(JDBC_URL, "jdbc:hsqldb:mem:${persistenceUnitName}")
                .property(SCHEMAGEN_DATABASE_ACTION, SPEC_ACTION_DROP_AND_CREATE)
                .property(DIALECT, HSQLDialect())
                .property(CONNECTION_HANDLING, IMMEDIATE_ACQUISITION_AND_HOLD)
                .property(AUTOCOMMIT, false)
                .property(ISOLATION, TRANSACTION_SERIALIZABLE)
        
        lateinit var emf: EntityManagerFactory
        
        @BeforeAll
        @JvmStatic
        fun createSchema() {
            emf = persistenceConfiguration.createEntityManagerFactory()
        }
        
        @AfterAll
        @JvmStatic
        fun cleanUp() {
            if (::emf.isInitialized) emf.close()
        }
    }
}

