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
import org.hibernate.envers.Audited

import javax.persistence.*
import javax.persistence.Entity
import javax.persistence.Table

@Audited
@Entity(name = "FileData")
@Table(name = "file_data")
@JsonIgnoreProperties(ignoreUnknown = true)
@FilterDefs(FilterDef(name = "scopeSecurity", parameters = arrayOf(ParamDef(name = "scopeId", type = "long"))))
@Filters(Filter(name = "scopeSecurity", condition = "(scope_id = :scopeId or scope_id is null)"))
class FileData : Model() {

    @Basic(fetch = FetchType.LAZY)
    @Lob
    var data: ByteArray? = null

    @Basic(fetch = FetchType.LAZY)
    @Lob
    @Column(columnDefinition = "TEXT")
    var charData: String? = null
}
