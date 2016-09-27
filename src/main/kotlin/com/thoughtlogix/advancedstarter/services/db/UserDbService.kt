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
import com.thoughtlogix.advancedstarter.models.users.User
import com.thoughtlogix.advancedstarter.services.Service
import com.thoughtlogix.advancedstarter.utils.Crypto
import org.apache.commons.lang3.StringUtils
import java.util.*

class UserDbService(jpa: JPA) : AbstractDbService(jpa), Service<User> {

    ////////////////////////////////////////////////////////////////////
    // Interface
    ////////////////////////////////////////////////////////////////////
    override val all: List<User>
        get() = findAll(User::class.java)

    override fun getById(id: UUID): User? {
        return findById(User::class.java, id)
    }

    override fun save(item: User): User {

        if (item.profile != null) {
            val profileDbService = ProfileDbService(jpa)
            profileDbService.save(item.profile as Profile)
        }

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

    override fun getPagedData(pageParams: PageParams): PagedData<User> {

        val query = jpa.entityManager!!.createQuery("from User entity " + buildWhereClause(pageParams, arrayOf("ref", "name", "notes")) + buildOrderByClause(pageParams), User::class.java)
        if (!StringUtils.isEmpty(pageParams.filter)) {
            query.setParameter("filter", filterWildcard(pageParams))
        }

        val items = query.setFirstResult(pageParams.skip).setMaxResults(pageParams.size).resultList
        return PagedData(items, countAllEntities<Any>("users"))
    }

    ////////////////////////////////////////////////////////////////////
    // Auditing
    ////////////////////////////////////////////////////////////////////
    override fun getRevisions(id: UUID): List<Any> {
        return findEntityRevisions(User::class.java, id)
    }

    override fun getRevision(id: UUID, rev: Int): User {
        return findEntityRevision(User::class.java, id, rev)
    }

    ////////////////////////////////////////////////////////////////////
    // Custom Queries
    ////////////////////////////////////////////////////////////////////
    fun getByUserName(username: String): User? {
        try {
            return jpa.entityManager!!.createQuery("from User entity where entity.username = :username", User::class.java).setParameter("username", username).singleResult
        } catch (e: Exception) {
            return null
        }

    }

    fun validate(username: String, password: String, host: String, userAgent: String): User? {
        try {
            val user = jpa.entityManager!!.createQuery("select u from User u where u.username = :username " + "and u.isActive = true", User::class.java).setParameter("username", username).singleResult
            if (user != null && Crypto.validatePassword(password, user.password, user.seed)) {
                user.lastIP = host
                user.lastLogin = Date()
                //                user.setAuthToken(Crypto.generateMD5(user.getSeed() + host + userAgent));
                user.authToken = Crypto.generateMD5(user.seed)
                return save(user)
            }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    fun getByAuthToken(authToken: String?, host: String, userAgent: String): User? {
        if (authToken == null) {
            return null
        }

        try {
            val user = jpa.entityManager!!.createQuery("from User entity where entity.authToken = :authToken and entity.isActive = true", User::class.java).setParameter("authToken", authToken).singleResult

            //            if (user != null && Crypto.generateMD5(user.getSeed() + host +  userAgent).equalsIgnoreCase(user.getAuthToken())) {
            if (user != null && Crypto.generateMD5(user.seed).equals(user.authToken!!, ignoreCase = true)) {
                return user
            }
            return null
        } catch (e: Exception) {
            return null
        }
    }

    fun getByUsernameAndEmail(username: String, email: String): User? {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(email)) {
            return null
        }
        try {
            val user = jpa.entityManager!!.createQuery("from User entity where entity.username = :username and entity.email = :email", User::class.java).setParameter("username", username).setParameter("email", email).singleResult

            return user
        } catch (e: Exception) {
            return null
        }

    }
}
