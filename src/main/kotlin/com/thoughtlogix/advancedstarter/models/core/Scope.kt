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
import org.hibernate.envers.Audited

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@Audited
@Entity(name = "Scope")
@Table(name = "scope")
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
class Scope : Model() {

    @Column(name = "name", length = 100, nullable = true, unique = true)
    @XmlElement
    var name: String = ""

    @Column(name = "name_long", length = 100, nullable = true, unique = true)
    @XmlElement
    var nameLong: String? = null

    @Column(name = "default_email", length = 100, nullable = true, unique = false)
    @XmlElement
    var defaultEmail: String? = null

    @Column(name = "default_address", length = 255, nullable = true, unique = false)
    @XmlElement
    var defaultAddress: String? = null

    @Column(name = "url", length = 255, nullable = true, unique = false)
    @XmlElement
    var url: String? = null

    @Column(name = "ref", length = 50, nullable = true, unique = false)
    @XmlElement
    var ref: String? = null

    @Column(name = "web_title", length = 255, nullable = true, unique = false)
    @XmlElement
    var webTitle: String? = null

    @Column(name = "web_keywords", length = 255, nullable = true, unique = false)
    @XmlElement
    var webKeywords: String? = null

    @Column(name = "web_description", length = 255, nullable = true, unique = false)
    @XmlElement
    var webDescription: String? = null

    @XmlElement
    @Column(name = "is_spa", unique = false, nullable = true)
    private var isSPA: Boolean? = null

    @XmlElement
    @Column(name = "is_active", unique = false, nullable = true)
    var isActive: Boolean? = null

}
