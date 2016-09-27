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

package com.thoughtlogix.advancedstarter.models.examples

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.thoughtlogix.advancedstarter.db.JPA
import com.thoughtlogix.advancedstarter.models.core.Model
import org.hibernate.envers.Audited
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Index
import javax.persistence.Table
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@Audited
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(name = "Todo")
@Table(name = "todo", indexes = arrayOf(Index(columnList = "title")))
class Todo : Model() {

    @XmlElement
    @NotNull(message = "validate.required")
    @Size(min = 3, max = 150, message = "validate.length.title")
    @Column(name = "title", length = 150, nullable = false, unique = false)
    var title: String = ""

    @XmlElement
    @Column(name = "is_complete", unique = false)
    var isComplete: Boolean = false

    override val dataTitle: String
        get() = title

    override fun toString(): String {
        return title
    }

    override fun onCreate(jpa: JPA) {
        super.onCreate(jpa)
    }

    override fun onUpdate(jpa: JPA) {
        super.onUpdate(jpa)
    }
}