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
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Index
import javax.persistence.Table
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@Audited
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(name = "Profile")
@Table(name = "profile", indexes = arrayOf(Index(columnList = "email"), Index(columnList = "age")))
open class Profile : Model() {

    @XmlElement
    @Column(name = "first_name", length = 50, nullable = true, unique = false)
    var firstName: String? = null

    @XmlElement
    @Column(name = "middle_name", length = 50, nullable = true, unique = false)
    var middleName: String? = null

    @XmlElement
    @Column(name = "last_name", length = 50, nullable = true, unique = false)
    var lastName: String? = null

    @XmlElement
    @Column(name = "company", length = 50, nullable = true, unique = false)
    var company: String? = null

    @XmlElement
    @Column(name = "department", length = 50, nullable = true, unique = false)
    var department: String? = null

    @XmlElement
    @Column(name = "title", length = 50, nullable = true, unique = false)
    open var title: String = ""

    @XmlElement
    @Column(name = "email", length = 100, nullable = true, unique = false)
    var email: String? = null

    @XmlElement
    @Column(name = "phone", length = 20, nullable = true, unique = false)
    var phone: String? = null

    @XmlElement
    @Column(name = "phone_home", length = 20, nullable = true, unique = false)
    var phoneHome: String? = null

    @XmlElement
    @Column(name = "phone_mobile", length = 20, nullable = true, unique = false)
    var phoneMobile: String? = null

    @XmlElement
    @Column(name = "fax", length = 20, nullable = true, unique = false)
    var fax: String? = null

    @XmlElement
    @Column(name = "dob", nullable = true, unique = false)
    var dob: Date? = null

    @XmlElement
    @Column(name = "age", nullable = true, unique = false)
    var age: Int? = null

    @XmlElement
    @Column(name = "ssn", length = 20, nullable = true, unique = false)
    var ssn: String? = null

    @XmlElement
    @Column(name = "address1", length = 100, nullable = true, unique = false)
    var address1: String? = null

    @XmlElement
    @Column(name = "address2", length = 100, nullable = true, unique = false)
    var address2: String? = null

    @XmlElement
    @Column(name = "city", length = 50, nullable = true, unique = false)
    var city: String? = null

    @XmlElement
    @Column(name = "state", length = 50, nullable = true, unique = false)
    var state: String? = null

    @XmlElement
    @Column(name = "zip", length = 20, nullable = true, unique = false)
    var zip: String? = null

    @XmlElement
    @Column(name = "country", length = 50, nullable = true, unique = false)
    var country: String? = null

    @XmlElement
    @Column(name = "gender", nullable = true, unique = false)
    var gender: Int = 0

    @XmlElement
    @Column(name = "greeting", nullable = true, unique = false)
    var greeting: Int = 0

    @XmlElement
    @Column(name = "suffix", nullable = true, unique = false)
    var suffix: Int = 0

    @XmlElement
    @Column(name = "icons", length = 100, nullable = true, unique = false)
    var icon: String? = null

    @XmlElement
    @Column(name = "skype_username", length = 50, nullable = true, unique = false)
    var skypeUsername: String? = null

    @XmlElement
    @Column(name = "twitter_username", length = 50, nullable = true, unique = false)
    var twitterUsername: String? = null

    @XmlElement
    @Column(name = "linkedin_username", length = 50, nullable = true, unique = false)
    var linkedinUsername: String? = null

    @XmlElement
    @Column(name = "facebook_username", length = 50, nullable = true, unique = false)
    var facebookUsername: String? = null

    @XmlElement
    @Column(name = "is_public", length = 50, nullable = true, unique = false)
    var isPublic: Boolean? = null

    @XmlElement
    @Column(name = "website", length = 100, nullable = true, unique = false)
    var website: String? = null

    @Column(name = "about", nullable = true, unique = false, columnDefinition = "TEXT")
    var about: String? = null

    @Column(name = "pubs", nullable = true, unique = false, columnDefinition = "TEXT")
    var pubs: String? = null

    @XmlElement
    @Column(name = "ref", length = 50, nullable = true, unique = false)
    open var ref: String? = null

    override val dataTitle: String
        get() = toString()

    override fun toString(): String {
        return ref ?: ""
    }
}
