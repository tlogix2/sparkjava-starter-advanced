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
import org.hibernate.annotations.*

import javax.persistence.*
import javax.persistence.Entity
import javax.persistence.Table
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
@Inheritance(strategy = InheritanceType.JOINED)
@Entity(name = "Log")
@Table(name = "log")
@FilterDefs(FilterDef(name = "scopeSecurity", parameters = arrayOf(ParamDef(name = "scopeId", type = "long"))))
@Filters(Filter(name = "scopeSecurity", condition = "(scope_id = :scopeId or scope_id is null)"))
class Log : Model() {

    @XmlElement
    @Column(name = "session", length = 50, nullable = true, unique = false)
    var session: String? = null

    @XmlElement
    @Column(name = "module", length = 50, nullable = true, unique = false)
    var module: String? = null

    @XmlElement
    @Column(name = "action", length = 50, nullable = true, unique = false)
    var action: String? = null

    @XmlElement
    @Column(name = "params", length = 255, nullable = true, unique = false)
    var params: String? = null

    @XmlElement
    @Column(name = "public_ip", length = 20, nullable = true, unique = false)
    var publicIp: String? = null

    @XmlElement
    @Column(name = "domain", length = 50, nullable = true, unique = false)
    var domain: String? = null

    @XmlElement
    @Column(name = "format", length = 10, nullable = true, unique = false)
    var format: String? = null

    @XmlElement
    @Column(name = "request_method", length = 50, nullable = true, unique = false)
    var requestMethod: String? = null

    @XmlElement
    @Column(name = "is_ajax", nullable = true, unique = false)
    var isAjax: Boolean = false
}