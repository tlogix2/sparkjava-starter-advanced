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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(name = "Starred")
@Table(name = "starred")
class Starred() : Model() {

    constructor(name: String, objectUuid: UUID, dataType: String) : this() {
        this.name = name
        this.objectUuid = objectUuid
        this.dataType = dataType
    }

    @XmlElement
    @Column(name = "name", length = 50, nullable = true, unique = false)
    var name: String = ""

    @XmlElement
    @Column(name = "object_uuid", nullable = false, unique = false)
    var objectUuid: UUID = UUID.randomUUID()

    @XmlElement
    @Column(name = "data_type", length = 50, nullable = true, unique = false)
    var dataType: String = ""

    companion object {

        fun isStarred(starredList: List<Starred>, model: Model, username: String): Boolean {
            for (starred in starredList) {
                if (starred.objectUuid!!.equals(model.getIdAsUuid()) && starred.createdBy!!.equals(username)) {
                    return true
                }
            }
            return false
        }
    }

}