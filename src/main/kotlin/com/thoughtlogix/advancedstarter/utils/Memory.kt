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

package com.thoughtlogix.advancedstarter.utils

import org.slf4j.LoggerFactory

object Memory {

    val logger = LoggerFactory.getLogger(LoggerFactory::class.java)
    internal val mb = 1024 * 1024
    internal var runtime = Runtime.getRuntime()

    fun print() {
        logger.info(String.format("Memory (Used/Free/Total/Max): %s/%s/%s/%s Mb", used, free, total, max))
    }

    val used: Long
        get() = (runtime.totalMemory() - runtime.freeMemory()) / mb

    val free: Long
        get() = runtime.freeMemory() / mb

    val total: Long
        get() = runtime.totalMemory() / mb

    val max: Long
        get() = runtime.maxMemory() / mb
}
