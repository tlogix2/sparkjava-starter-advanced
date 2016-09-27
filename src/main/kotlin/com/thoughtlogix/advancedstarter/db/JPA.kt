/*
 * MIT License
 *
 * Copyright (c) 2016 Thought Logix
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.thoughtlogix.advancedstarter.db

import com.thoughtlogix.advancedstarter.app.settings.system.DatabaseSettings
import org.slf4j.LoggerFactory
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.EntityTransaction
import javax.persistence.Persistence

/**
 * A utility class to handle JPA persistence.
 */
class JPA(databaseSettings: DatabaseSettings) {

    val logger = LoggerFactory.getLogger(JPA::class.java)

    /**
     * The entity manager factory
     */
    private val emf: EntityManagerFactory

    /**
     * Thread local entity manager
     */
    private val threadEntityManager = ThreadLocal<EntityManager>()

    /**
     * Thread local transaction
     */
    private val threadTransaction = ThreadLocal<EntityTransaction>()

    /**
     * Thread local user
     */
    private val threadContext = ThreadLocal<Context>()

    init {
        try {
            logger.debug("Initializing EntityManagerFactory")
            emf = Persistence.createEntityManagerFactory("defaultPersistenceUnit", getConfigProperties(databaseSettings))
        } catch (ex: Throwable) {
            //            Logger.error("error initializing EntityManagerFactory", ex);
            throw ExceptionInInitializerError(ex)
        }

    }

    /**
     * Get the current (thread local) context (user info, session, server stuff).

     * @return thread local context
     */
    val context: Context
        get() {
            var ctx: Context? = threadContext.get()
            if (ctx == null) {
                ctx = Context()
                threadContext.set(ctx)
            }
            return ctx
        }

    /**
     * Get the current (thread local) entity manager

     * @return thread local entity manager
     */
    val entityManager: EntityManager?
        get() {
            var em: EntityManager? = threadEntityManager.get()
            if (em == null) {
                em = emf.createEntityManager()
                threadEntityManager.set(em)
            }
            return em
        }

    /**
     * Close the current (thread local) entity manager
     */
    fun closeEntityManager() {
        val em = threadEntityManager.get()
        threadEntityManager.set(null)
        if (em != null && em.isOpen) {
            em.close()
        }
    }

    /**
     * Begin transaction for current (thread local) entity manager
     */
    fun beginTransaction() {
        var tx: EntityTransaction? = threadTransaction.get()
        if (tx == null) {
            tx = entityManager!!.transaction
            threadTransaction.set(tx)
        }
        if (!tx!!.isActive) {
            tx.begin()
        }
    }

    /**
     * Commit current (thread local) transaction
     */
    fun commitTransaction() {
        val tx = threadTransaction.get()
        if (tx != null && tx.isActive && !tx.rollbackOnly) {
            tx.commit()
        }
        threadTransaction.set(null)
    }

    /**
     * Roll back current (thread local) transaction
     */
    fun rollbackTransaction() {
        val tx = threadTransaction.get()
        threadTransaction.set(null)
        if (tx != null && tx.isActive) {
            tx.rollback()
        }
    }

    /**
     * Convert database settings into generic properties used by JPA spec.

     * @param databaseSettings
     * *
     * @return Properties for EMF
     */
    fun getConfigProperties(databaseSettings: DatabaseSettings): Properties {
        val properties = Properties()

        /**
         * Core props
         */
        properties.put("javax.persistence.provider", "org.hibernate.jpa.HibernatePersistenceProvider")
        properties.put("javax.persistence.transactionType", "RESOURCE_LOCAL")

        /**
         * Connection props
         */
        properties.put("hibernate.dialect", databaseSettings.dialect)
        properties.put("hibernate.connection.driver_class", databaseSettings.driver)
        properties.put("hibernate.connection.url", databaseSettings.url)
        properties.put("hibernate.connection.username", databaseSettings.username)
        properties.put("hibernate.connection.password", databaseSettings.password)

        /**
         * Hibernate specific props
         */
        properties.put("hibernate.hbm2ddl.auto", "update") //create | update | create-drop | validate | none
        properties.put("hibernate.show_sql", "false")
        properties.put("hibernate.format_sql", "false")
        properties.put("hibernate.jdbc.batch_size", "1000")
        properties.put("hibernate.cache.use_second_level_cache", "true")

        /**
         * Envers auditing module props
         */
        properties.put("hibernate.integration.envers.enabled", "true")
        properties.put("hibernate.envers.autoRegisterListeners", "false")
        properties.put("org.hibernate.envers.revision_type_field_name", "revtype")
        properties.put("org.hibernate.envers.audit_table_suffix", "_audit")
        properties.put("org.hibernate.envers.revision_field_name", "rev")

        /**
         * c3p0 connection pooling props
         */
        properties.put("hibernate.c3p0.min_size", "5")
        properties.put("hibernate.c3p0.max_size", "100")
        properties.put("hibernate.c3p0.timeout", "100")
        properties.put("hibernate.c3p0.max_statements", "500")
        properties.put("hibernate.c3p0.idle_test_period", "3000")

        return properties
    }

}