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

package com.thoughtlogix.advancedstarter.services.db

import com.thoughtlogix.advancedstarter.db.JPA
import com.thoughtlogix.advancedstarter.db.PageParams
import com.thoughtlogix.advancedstarter.db.PagedData
import com.thoughtlogix.advancedstarter.models.core.Profile
import com.thoughtlogix.advancedstarter.services.Service
import org.apache.commons.lang3.StringUtils
import java.util.*

class ProfileDbService(jpa: JPA) : AbstractDbService(jpa), Service<Profile> {

    ////////////////////////////////////////////////////////////////////
    // Interface
    ////////////////////////////////////////////////////////////////////
    override val all: List<Profile>
        get() = findAll(Profile::class.java)

    override fun getById(id: UUID): Profile? {
        return findById(Profile::class.java, id)
    }

    override fun save(item: Profile): Profile {
        if (isNew(item)) {
            item.onCreate(jpa)
            return create(item)
        } else {
            item.onUpdate(jpa)
            return update(item)
        }
    }

    override fun delete(id: UUID): Boolean {
        val item = getById(id)
        if (item != null) {
            delete(item)
            return true
        } else {
            return false
        }
    }

    override fun getPagedData(pageParams: PageParams): PagedData<Profile> {

        val query = jpa.entityManager!!.createQuery("from Profile entity " + buildWhereClause(pageParams, arrayOf("ref", "name", "notes")) + buildOrderByClause(pageParams), Profile::class.java)
        if (!StringUtils.isEmpty(pageParams.filter)) {
            query.setParameter("filter", filterWildcard(pageParams))
        }

        val items = query.setFirstResult(pageParams.skip).setMaxResults(pageParams.size).resultList
        return PagedData(items, countAllEntities<Any>("profile"))
    }

    ////////////////////////////////////////////////////////////////////
    // Auditing
    ////////////////////////////////////////////////////////////////////
    override fun getRevisions(id: UUID): List<Any> {
        return findEntityRevisions(Profile::class.java, id)
    }

    override fun getRevision(id: UUID, rev: Int): Profile {
        return findEntityRevision(Profile::class.java, id, rev)
    }

    ////////////////////////////////////////////////////////////////////
    // Custom Queries
    ////////////////////////////////////////////////////////////////////
    fun getByRef(ref: String): Profile {
        return jpa.entityManager!!.createQuery("from Profile entity where entity.ref = :ref", Profile::class.java).setParameter("ref", ref).singleResult
    }

}
