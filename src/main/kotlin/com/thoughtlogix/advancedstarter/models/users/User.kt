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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.thoughtlogix.advancedstarter.db.JPA
import com.thoughtlogix.advancedstarter.models.core.Model
import com.thoughtlogix.advancedstarter.models.core.Profile
import com.thoughtlogix.advancedstarter.models.core.Recent
import com.thoughtlogix.advancedstarter.models.core.Server
import com.thoughtlogix.advancedstarter.utils.Crypto
import org.hibernate.envers.Audited
import org.hibernate.validator.constraints.Email
import java.util.*
import javax.persistence.*
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
@Entity(name = "User")
@Table(name = "users", indexes = arrayOf(Index(columnList = "username"), Index(columnList = "authToken"), Index(columnList = "email")))
class User : Model() {

    @XmlElement
    @NotNull(message = "validate.required")
    @Size(min = 3, max = 50, message = "validate.length.user")
    @Column(name = "username", length = 50, nullable = false, unique = false)
    var username: String = ""

    @XmlElement
    @NotNull(message = "validate.required")
    @Size(min = 6, message = "validate.length.password")
    @Column(name = "password", length = 128, nullable = false, unique = false)
    var password: String = "dsfioudsiofuoweufiowfiowiouweiofuow"

    @XmlElement
    @Column(name = "authToken", length = 32, nullable = true, unique = false)
    var authToken: String? = null

    @XmlElement
    @NotNull(message = "validate.required")
    @Email(message = "validate.email")
    @Column(name = "email", length = 100, nullable = false, unique = false)
    var email: String? = null

    @XmlElement
    @Column(name = "is_active", unique = false)
    var isActive: Boolean? = null

    @XmlElement
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_id", nullable = true, unique = false)
    var profile: Profile? = null

    @Column(name = "seed", length = 32, nullable = true, unique = false)
    var seed: String = Crypto.generateSalt();

    @XmlElement
    @Column(name = "last_login", nullable = true, unique = false)
    var lastLogin: Date? = null

    @XmlElement
    @Column(name = "last_ip", length = 20, nullable = true, unique = false)
    var lastIP: String? = null

    @XmlElement
    @Column(name = "last_settings_id", nullable = true, unique = false)
    var lastSettingsId: String? = null

    @ManyToMany(fetch = FetchType.EAGER)
    @XmlElement
    @JoinTable(name = "map_role_user", joinColumns = arrayOf(JoinColumn(name = "user_id")), inverseJoinColumns = arrayOf(JoinColumn(name = "role_id")))
    var roles: Set<Role>? = HashSet()

    @ManyToMany(fetch = FetchType.EAGER)
    @XmlElement
    @JoinTable(name = "map_team_user", joinColumns = arrayOf(JoinColumn(name = "user_id")), inverseJoinColumns = arrayOf(JoinColumn(name = "team_id")))
    var teams: Set<Team>? = HashSet()

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "server_id", nullable = true, unique = false)
    @XmlElement
    var server: Server? = null

    @Basic(fetch = FetchType.EAGER)
    @Column(name = "settings", nullable = true, unique = false, columnDefinition = "TEXT")
    var settings: String? = null

    @XmlElement
    @OrderBy("updatedAt desc")
    @OneToMany(targetEntity = Recent::class, fetch = FetchType.EAGER, mappedBy = "user")
    var recent: Set<Recent>? = HashSet()

    var trackSettings: String? = null

    init {
        isActive = true
    }

    override val dataTitle: String
        get() = username

    //    private String createToken() {
    //        return UUID.randomUUID().toString();
    //    }
    //
    //    public void deleteAuthToken() {
    //        authToken = null;
    //        //        save();
    //    }
    override fun toString(): String {
        return username
    }

    override fun onCreate(jpa: JPA) {
        super.onCreate(jpa)
        if (password.length != 128 && !password.equals("", ignoreCase = true)) {
            password = Crypto.generatePasswordHash(seed, password)
        }
    }

    override fun onUpdate(jpa: JPA) {
        super.onUpdate(jpa)
        if (password.length != 128 && !password.equals("", ignoreCase = true)) {
            password = Crypto.generatePasswordHash(seed, password)
        }
    }

    val rolesAsString: String
        get() {
            val info = StringBuffer()
            if (roles == null) return ""
            val iter = this.roles!!.iterator()
            while (iter.hasNext()) {
                info.append(iter.next().name + " ")
            }
            return info.toString().trim()
        }

    val teamsAsString: String
        get() {
            val info = StringBuffer()
            if (teams == null) return ""
            val iter = this.teams!!.iterator()
            while (iter.hasNext()) {
                info.append(iter.next().name + " ")
            }
            return info.toString().trim()
        }

    val isBigShot: Boolean
        get() = hasRole(arrayOf("admin", "director", "manager"))

    fun hasRole(role: String): Boolean {
        return hasRole(arrayOf(role))
    }

    fun hasRole(roles: Array<String>): Boolean {
        val roleList = this.roles!!.toList()
        for (userRole in roleList) {
            for (role in roles) {
                if (role.equals(userRole.name, ignoreCase = true)) {
                    return true
                }
            }
        }
        return false
    }

    fun hasTeam(team: String): Boolean {
        return hasTeam(arrayOf(team))
    }

    fun hasTeam(teams: Array<String>): Boolean {
        val teamList = this.teams!!.toList()
        for (userTeam in teamList) {
            for (team in teams) {
                if (team.equals(userTeam.name, ignoreCase = true)) {
                    return true
                }
            }
        }
        return false
    }
}
