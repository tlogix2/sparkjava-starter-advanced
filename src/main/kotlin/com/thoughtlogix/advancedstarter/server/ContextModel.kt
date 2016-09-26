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

package com.thoughtlogix.advancedstarter.server

import java.util.*

/**
 * The ContextModel stores data for a request/responce lifecycle.
 */
class ContextModel {
    var model = ThreadLocal<HashMap<String, Any>>()

    init {
        reset()
    }

    fun put(key: String, value: Any) {
        model.get().put(key, value);
    }

    fun getModel(): Map<String, Any> {
        return model.get();
    }

    fun get(key: String): Any {
        return model.get()[key]!!;
    }

    fun containsKey(key: String): Boolean {
        return model.get().containsKey(key)
    }

    fun remove(key: String) {
        model.get().remove(key)
    }

    fun reset() {
        model.set(HashMap<String, Any>())
    }
}
