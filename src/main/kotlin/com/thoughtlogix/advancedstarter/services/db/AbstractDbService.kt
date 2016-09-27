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
import com.thoughtlogix.advancedstarter.db.Revision
import com.thoughtlogix.advancedstarter.models.core.Model
import org.apache.commons.lang3.StringUtils
import org.hibernate.envers.AuditReaderFactory
import org.hibernate.envers.DefaultRevisionEntity
import org.hibernate.envers.RevisionType
import org.hibernate.envers.query.AuditEntity
import org.slf4j.LoggerFactory
import java.math.BigInteger
import java.util.*
import javax.persistence.PersistenceException

abstract class AbstractDbService(protected var jpa: JPA) {

    val logger = LoggerFactory.getLogger(AbstractDbService::class.java)

    protected fun isNew(item: Model): Boolean {
        if (item.version === -1L) {
            return true
        } else {
            return false
        }
    }

    fun <T> create(item: T?): T {
        if (item == null) throw PersistenceException("Item may not be null")

        try {
            jpa.entityManager!!.persist(item)
        } catch (e: Exception) {
            logger.error(e.message, e)
        }

        return item
    }

    fun <T> update(item: T?): T {
        if (item == null) throw PersistenceException("Item may not be null")

        var mergedItem: T = item
        try {
            mergedItem = jpa.entityManager!!.merge(item)
        } catch (e: Exception) {
            logger.error(e.message, e)
        }

        return mergedItem
    }

    protected fun <T> findById(entityClass: Class<T>, id: UUID?): T? {
        if (id == null) throw PersistenceException("Id may not be null")
        return jpa.entityManager!!.find(entityClass, id)
    }

    fun <T> delete(item: T?) {
        if (item == null) throw PersistenceException("Item may not be null")
        try {
            jpa.entityManager!!.remove(item)
        } catch (e: Exception) {
            logger.error(e.message, e)
        }

    }

    protected fun <T> findAll(entityClass: Class<T>): List<T> {
        val criteriaBuilder = jpa.entityManager!!.criteriaBuilder
        val criteria = criteriaBuilder.createQuery(entityClass)
        val entityRoot = criteria.from(entityClass)
        criteria.select(entityRoot)
        return jpa.entityManager!!.createQuery(criteria).resultList
    }

    protected fun <T> findPagedData(entityClass: Class<T>, pageParams: PageParams): List<T> {
        val criteriaBuilder = jpa.entityManager!!.criteriaBuilder
        val criteria = criteriaBuilder.createQuery(entityClass)
        val entityRoot = criteria.from(entityClass)
        criteria.select(entityRoot)

        //        Predicate predicate = criteriaBuilder.conjunction();
        //        predicate = criteriaBuilder.and(criteriaBuilder.like(
        //                criteriaBuilder.lower(
        //                        entityRoot.get(
        //                                type.getDeclaredSingularAttribute("name", String.class)
        //                        )), "%" + pageParams.getFilter().toLowerCase() + "%"
        //        ));
        //
        //        Predicate predicate2 = criteriaBuilder.and(criteriaBuilder.like(
        //                criteriaBuilder.lower(
        //                        entityRoot.get(
        //                                type.getDeclaredSingularAttribute("address", String.class)
        //                        )), "%" + pageParams.getFilter().toLowerCase() + "%"
        //        ));
        //        criteria.where(criteriaBuilder.or(predicate, predicate2));

        val order = if (pageParams.sort.equals("asc", ignoreCase = true)) criteriaBuilder.asc(entityRoot.get<Any>(pageParams.order)) else criteriaBuilder.desc(entityRoot.get<Any>(pageParams.order))
        criteria.orderBy(order)
        return jpa.entityManager!!.createQuery(criteria).setFirstResult(pageParams.skip).setMaxResults(pageParams.size).resultList
    }

    protected fun <T> countAllEntities(tableName: String): Long {
        val result = jpa.entityManager!!.createNativeQuery("select count(*) from " + tableName).singleResult
        var count: Long = 0
        try {
            count = (result as BigInteger).toLong()
        } catch (e: Exception) {
            count = (result as Int).toLong()
        }

        return count
    }

    protected fun <T> countAllEntities(entityClass: Class<T>): Long {
        val criteriaBuilder = jpa.entityManager!!.criteriaBuilder
        val countQuery = criteriaBuilder.createQuery(Long::class.java)
        countQuery.select(criteriaBuilder.count(countQuery.from(entityClass)))
        return jpa.entityManager!!.createQuery(countQuery).singleResult
    }

    protected fun <T : Model> findEntityRevisions(entityClass: Class<T>, id: UUID): List<Any> {
        val query = AuditReaderFactory.get(jpa.entityManager!!).createQuery().forRevisionsOfEntity(entityClass, false, true)
        query.add(AuditEntity.id().eq(id))
        val objects = query.resultList
        val revisionList = ArrayList<Revision>()

        for (i in objects.indices) {
            val objArray = objects[i] as Array<Any>
            val revision = Revision()
            revision.localId = i.toString()
            revision.revisionId = (objArray[1] as DefaultRevisionEntity).id.toString()
            revision.revisionDate = (objArray[1] as DefaultRevisionEntity).revisionDate.toString()
            revision.revisionOperation = (objArray[2] as RevisionType).name
            revision.objectName = (objArray[0] as T).toString()
            revision.objectId = (objArray[0] as T).getId() + ""
            revisionList.add(revision)
        }
        return revisionList
    }

    protected fun <T : Model> findEntityRevision(entityClass: Class<T>, id: UUID, rev: Int): T {
        return AuditReaderFactory.get(jpa.entityManager!!).find(entityClass, id, rev)
    }

    protected fun cleanSearchParameter(value: String): String {
        return value.replace("*", "%")
    }

    protected fun buildOrderByClause(pageParams: PageParams): String {
        var entityName = ""
        if (!pageParams.order.contains(".")) entityName = "entity."

        return String.format("ORDER BY %s %s", entityName + pageParams.order, pageParams.sort)
    }

    protected fun buildWhereClause(pageParams: PageParams, fields: Array<String>): String {
        if (StringUtils.isEmpty(pageParams.filter)) {
            return ""
        }

        val result = StringBuilder()
        for (field in fields) {
            result.append(String.format(" entity.%s {{operator}} :filter ", field))
            result.append("OR")
        }
        val clause = if (result.length > 0) result.substring(0, result.length - 2) else ""
        return filterComparison(" where " + clause, pageParams) + " "
    }

    protected fun filterWildcard(pageParams: PageParams): String {
        return pageParams.filter.replace("*", "%").trim { it <= ' ' }
    }

    protected fun filterComparison(queryString: String, pageParams: PageParams): String {
        if (pageParams.filter.contains("*"))
            return queryString.replace("{{operator}}", "like")
        else
            return queryString.replace("{{operator}}", "=")
    }

    /*
     * Old ver based on JPQL.  Open to sql injection with order by added.
     *
    public PagedData<Lab> getPagedDataOld(PageParams pageParams) {
        List<Lab> items = jpa.getEntityManager()
                .createQuery("from Lab lab order by lab.id desc", Lab.class)
                .setFirstResult(pageParams.getSkip()).setMaxResults(pageParams.getSize())
                .getResultList();
        BigInteger count = (BigInteger) jpa.getEntityManager().createNativeQuery("select count(*) from lab").getSingleResult();
        return new PagedData<Lab>(items, count.longValue());
    }
     */
}
