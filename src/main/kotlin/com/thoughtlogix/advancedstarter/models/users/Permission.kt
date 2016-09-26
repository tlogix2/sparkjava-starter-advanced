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

package com.thoughtlogix.advancedstarter.models.users

import com.thoughtlogix.advancedstarter.models.core.Model
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.envers.Audited
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import javax.xml.bind.annotation.*

@Audited
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(name = "Permission")
@Table(name = "permission", indexes = arrayOf(Index(columnList = "name")))
class Permission : Model() {

    @XmlElement
    @NotNull(message = "validate.required")
    @Size(min = 3, max = 100, message = "validate.length")
    @Column(name = "name", length = 100, nullable = false, unique = false)
    var name: String = ""

    @XmlElement
    @Column(name = "module", length = 50, nullable = false, unique = false)
    var module: String? = null

    @XmlElement
    @Column(name = "controller", length = 50, nullable = false, unique = false)
    var controller: String? = null

    @XmlElement
    @Column(name = "action", length = 50, nullable = false, unique = false)
    var action: String? = null

    @XmlElement
    @Column(name = "is_allowed", unique = false)
    var isAllowed: Boolean? = null

    @ManyToMany
    @XmlTransient
    @JsonIgnore
    @JoinTable(name = "map_role_permission", joinColumns = arrayOf(JoinColumn(name = "permission_id")), inverseJoinColumns = arrayOf(JoinColumn(name = "role_id")))
    var roles: Set<Permission>? = HashSet()

    override val dataTitle: String
        get() = name
}
