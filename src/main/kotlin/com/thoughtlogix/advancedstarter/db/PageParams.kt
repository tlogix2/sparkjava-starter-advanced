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

package com.thoughtlogix.advancedstarter.db

class PageParams() {

    var page = 0
        set(value) = if (value >= 0) field = value else field = 0
    var size = 15
    var sort = "desc"
    var order = "id"
    var filter = ""
        set(value) {
            field = value.replace("\r", " ").replace("\n", " ")
        }
    var expression = ""
    var currentSort: String? = null
        private set
    var currentOrder: String? = null
        private set

    constructor(pg: Int, sz: Int, orderBy: String, sortBy: String) : this() {
        page = pg
        size = sz
        order = orderBy
        sort = sortBy
    }

    fun setLastSort(currentSort: String) {
        this.currentSort = currentSort
    }

    fun setLastOrder(currentOrder: String) {
        this.currentOrder = currentOrder
    }

    val skip: Int
        get() = page * size

    val orderBy: String
        get() = String.format("%s %s", order, sort)

    val queryString: String
        get() = String.format("?page=%s&size=%s&order=%s&sort=%s&filter=%s&expression=%s", page, size, order, sort, filter, expression)
}
