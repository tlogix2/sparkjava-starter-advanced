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
import com.thoughtlogix.advancedstarter.models.core.Model
import java.util.*

class GenericDbService(jpa: JPA, private val entityClass: Class<*>) : AbstractDbService(jpa) {

    val all: List<Any>
        get() = findAll(entityClass)

    fun getById(id: UUID): Any? {
        return findById(entityClass, id)
    }

    fun <T : Model> save(item: T): T {
        if (isNew(item)) {
            item.onCreate(jpa)
            return create(item)
        } else {
            item.onUpdate(jpa)
            return update(item)
        }
    }

    fun delete(id: UUID): Boolean {
        val item = getById(id)
        if (item != null) {
            delete(item)
            return true
        } else {
            return false
        }
    }

    fun getPagedData(pageParams: PageParams): PagedData<Any> {
        return PagedData(findPagedData(entityClass, pageParams), countAllEntities(entityClass))
    }

}
