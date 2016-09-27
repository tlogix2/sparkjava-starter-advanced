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

package com.thoughtlogix.advancedstarter.models.users

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.thoughtlogix.advancedstarter.models.core.Model
import org.hibernate.envers.Audited
import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import javax.xml.bind.annotation.*

@Audited
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(name = "Role")
@Table(name = "role", indexes = arrayOf(Index(columnList = "name")))
class Role() : Model() {

    constructor(name: String) : this() {
        this.name = name;
    }

    @XmlElement
    @NotNull(message = "validate.required")
    @Size(min = 3, max = 100, message = "validate.length")
    @Column(name = "name", length = 100, nullable = false, unique = false)
    var name: String = ""

    @ManyToMany(fetch = FetchType.EAGER)
    @XmlElement
    @JoinTable(name = "map_role_permission", joinColumns = arrayOf(JoinColumn(name = "role_id")), inverseJoinColumns = arrayOf(JoinColumn(name = "permission_id")))
    var permissions: Set<Permission>? = null

    @ManyToMany(fetch = FetchType.EAGER)
    @XmlTransient
    @JsonIgnore
    @JoinTable(name = "map_role_user", joinColumns = arrayOf(JoinColumn(name = "role_id")), inverseJoinColumns = arrayOf(JoinColumn(name = "user_id")))
    var users: Set<User>? = null

    override val dataTitle: String
        get() = name
}
