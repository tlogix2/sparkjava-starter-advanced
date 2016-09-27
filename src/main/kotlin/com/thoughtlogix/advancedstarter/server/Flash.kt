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

package com.thoughtlogix.advancedstarter.server

import com.thoughtlogix.advancedstarter.models.constants.FlashMessageType
import java.util.*

/**
 * Flash stores temporary messages for one response/request cycle.  One flash object exists
 * across the server and messages are keyed based on sessionid (or authtoken for authenticated users)
 */
object Flash {

    private val messages = HashMap<String, FlashItem>()

    fun getMessage(key: String): String {
        val flashItem = messages[key] ?: return ""
        val msg = flashItem.message
        messages.remove(key)
        return msg
    }

    fun getType(key: String): Int {
        val flashItem = messages[key] ?: return 0
        return flashItem.type.toInt()
    }

    fun isInfo(key: String): Boolean {
        val flashItem = messages[key] ?: return false
        return flashItem.type == FlashMessageType.INFO
    }

    fun isWarning(key: String): Boolean {
        val flashItem = messages[key] ?: return false
        return flashItem.type == FlashMessageType.WARNING
    }

    fun isError(key: String): Boolean {
        val flashItem = messages[key] ?: return false
        return flashItem.type == FlashMessageType.ERROR
    }

    fun addInfo(key: String, msg: String) {
        messages.put(key, FlashItem(msg, FlashMessageType.INFO))
    }

    fun addWarning(key: String, msg: String) {
        messages.put(key, FlashItem(msg, FlashMessageType.WARNING))
    }

    fun addError(key: String, msg: String) {
        messages.put(key, FlashItem(msg, FlashMessageType.ERROR))
    }
}
