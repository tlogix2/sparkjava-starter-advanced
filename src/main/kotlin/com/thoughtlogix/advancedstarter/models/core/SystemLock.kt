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

package com.thoughtlogix.advancedstarter.models.core

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@Entity(name = "SystemLock")
@Table(name = "system_lock")
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
class SystemLock : Model() {

    @XmlElement
    @NotNull(message = "validate.required")
    @Size(min = 3, max = 100, message = "validate.length")
    @Column(name = "title", length = 100, nullable = false, unique = false)
    var title: String = ""

    @XmlElement
    @Column(name = "object_uuid", nullable = false, unique = true)
    var objectUuid: UUID? = null

    @XmlElement
    var systemClass: Int = 0

    override fun toString(): String {
        return title
    }

    fun getObjectUuid(): String {
        if (objectUuid != null) {
            return objectUuid!!.toString()
        } else {
            return ""
        }
    }

    fun setObjectUuid(objectUuid: String) {
        this.objectUuid = UUID.fromString(objectUuid)
    }

    override val dataTitle: String
        get() = title

    companion object {

        fun isLocked(locks: List<SystemLock>, model: Model): Boolean {
            for (lock in locks) {
                if (lock.objectUuid!!.equals(model.getIdAsUuid())) {
                    return true
                }
            }
            return false
        }
    }
}
