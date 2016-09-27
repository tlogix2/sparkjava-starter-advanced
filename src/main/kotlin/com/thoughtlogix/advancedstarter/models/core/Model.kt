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

package com.thoughtlogix.advancedstarter.models.core

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.thoughtlogix.advancedstarter.Lang
import com.thoughtlogix.advancedstarter.db.JPA
import com.thoughtlogix.advancedstarter.models.ManagedData
import com.thoughtlogix.advancedstarter.utils.DateTimeUtils
import java.io.Serializable
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
abstract class Model : Serializable, ManagedData {

    @Id
    //    @Column(name = "id", columnDefinition="binary(16)" )
    @Column(name = "id", columnDefinition = "uuid")
    //    @Column(name = "id", columnDefinition="uniqueidentifier" )
    @NotNull
    private var id: UUID = UUID.randomUUID()

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    var createdAt: Date = Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    var updatedAt: Date = Date();

    @Column(name = "created_by", length = 50, nullable = true, unique = false)
    var createdBy: String = "na"

    @Column(name = "updated_by", length = 50, nullable = true, unique = false)
    var updatedBy: String = "na"

    @Column(name = "notes", nullable = true, unique = false, columnDefinition = "TEXT")
    var notes: String = ""

    @Version
    @NotNull
    var version: Long? = -1L

    @Transient
    var temp: String? = null

    open fun onUpdate(jpa: JPA) {

        var username: String = "sys"
        val user = jpa.context.user
        if (user != null && user.username != null) {
            username = user?.username
        }

        if (createdAt == null) {
            createdAt = Date()
        }
        if (createdBy == null || createdBy!!.equals("", ignoreCase = true)) {
            createdBy = username
        }

        updatedAt = Date()
        updatedBy = username

    }

    open fun onCreate(jpa: JPA) {

        var username: String = "sys"
        val user = jpa.context.user
        if (jpa.context.user != null && jpa.context.user.username != null) {
            username = jpa.context.user.username
        }
        createdAt = Date()
        updatedAt = Date()
        createdBy = username
        updatedBy = username

    }

    fun getId(): String {
        return id.toString()
    }

    fun setId(id: String) {
        this.id = UUID.fromString(id)
    }

    fun getIdAsUuid(): UUID {
        return id
    }

    override fun hashCode(): Int {
        return id!!.hashCode()
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null) return false
        if (obj !is Model) {
            return false
        }
        return getId() == obj.getId()
    }

    override val dataId: String
        get() = getId()

    override val dataTitle: String
        get() = getId()

    override val dataDescription: String
        get() = Lang.tr("created") + " " + DateTimeUtils.formatToLongDate(createdAt) + " " + Lang.tr("by") + " " + createdBy

    fun setAsUuid(uuid: UUID) {
        this.id = uuid
    }

}
