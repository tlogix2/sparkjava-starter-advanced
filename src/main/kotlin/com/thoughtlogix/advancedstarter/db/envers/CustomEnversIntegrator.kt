/*
 * MIT License
 *
 * Copyright (c) $date.year Thought Logix
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

package com.thoughtlogix.advancedstarter.db.envers

import com.thoughtlogix.advancedstarter.db.JPA
import org.hibernate.boot.Metadata
import org.hibernate.engine.spi.SessionFactoryImplementor
import org.hibernate.envers.boot.internal.EnversService
import org.hibernate.envers.event.spi.EnversListenerDuplicationStrategy
import org.hibernate.event.service.spi.EventListenerRegistry
import org.hibernate.event.spi.EventType
import org.hibernate.integrator.spi.Integrator
import org.hibernate.service.spi.SessionFactoryServiceRegistry
import org.slf4j.LoggerFactory


class CustomEnversIntegrator : Integrator {

    override fun integrate(metadata: Metadata, sessionFactory: SessionFactoryImplementor, serviceRegistry: SessionFactoryServiceRegistry) {

        val logger = LoggerFactory.getLogger(CustomEnversIntegrator::class.java)

        val listenerRegistry = serviceRegistry.getService(EventListenerRegistry::class.java)
        listenerRegistry.addDuplicationStrategy(EnversListenerDuplicationStrategy.INSTANCE)

        val enversService = serviceRegistry.getService(EnversService::class.java)
        if (!enversService.isInitialized) {
            logger.info("Envers version tracking is disabled")
            return
        }

        if (enversService.entitiesConfigurations.hasAuditedEntities()) {
            listenerRegistry.appendListeners(EventType.POST_DELETE, CustomEnversPostDeleteEventListenerImpl(enversService))
            listenerRegistry.appendListeners(EventType.POST_INSERT, CustomEnversPostInsertEventListenerImpl(enversService))
            listenerRegistry.appendListeners(EventType.POST_UPDATE, CustomEnversPostUpdateEventListenerImpl(enversService))
            listenerRegistry.appendListeners(EventType.POST_COLLECTION_RECREATE, CustomEnversPostCollectionRecreateEventListenerImpl(enversService))
            listenerRegistry.appendListeners(EventType.PRE_COLLECTION_REMOVE, CustomEnversPreCollectionRemoveEventListenerImpl(enversService))
            listenerRegistry.appendListeners(EventType.PRE_COLLECTION_UPDATE, CustomEnversPreCollectionUpdateEventListenerImpl(enversService))
        }
    }

    override fun disintegrate(sessionFactory: SessionFactoryImplementor, serviceRegistry: SessionFactoryServiceRegistry) {
        // nothing to do afaik
    }

}
