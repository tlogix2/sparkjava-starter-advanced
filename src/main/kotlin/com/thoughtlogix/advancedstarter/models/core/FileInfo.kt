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

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.*
import org.hibernate.envers.Audited
import java.util.*
import javax.persistence.*
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.Index
import javax.persistence.Table
import javax.validation.constraints.NotNull
import javax.xml.bind.annotation.*

@Audited
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(name = "FileInfo")
@Table(name = "file_info", indexes = arrayOf(Index(columnList = "name"), Index(columnList = "object_uuid"), Index(columnList = "object_uuid, type")))
class FileInfo : Model() {

    @XmlElement
    @Column(name = "name", length = 50, nullable = true, unique = false)
    var name: String = ""

    @XmlElement
    @NotNull(message = "validate.required")
    @Column(name = "object_uuid", length = 36, nullable = true, unique = false)
    private var objectUuid: UUID? = null

    @XmlElement
    @Column(name = "path", length = 255, nullable = true, unique = false)
    var path: String? = null

    @XmlElement
    @Column(name = "size", length = 50, nullable = true, unique = false)
    var size: String? = null

    @XmlElement
    @Column(name = "checksum", length = 50, nullable = true, unique = false)
    var checksum: String? = null

    @Column(name = "type", nullable = false, unique = false)
    var fileType: Int = 0

    @XmlElement
    @Column(name = "is_public", nullable = true, unique = false)
    var isPublic: Boolean? = null

    @XmlTransient
    @JsonIgnore
    @OneToOne(cascade = arrayOf(CascadeType.ALL), fetch = FetchType.LAZY)
    var fileData: FileData? = null

    override val dataTitle: String
        get() = name

    fun getObjectUuid(): String {
        return objectUuid!!.toString()
    }

    fun setObjectUuid(objectUuid: String) {
        this.objectUuid = UUID.fromString(objectUuid)
    }
}
