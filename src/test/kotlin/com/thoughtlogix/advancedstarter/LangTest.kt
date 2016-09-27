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

package com.infoquant.gf.common

import com.thoughtlogix.advancedstarter.Lang
import org.testng.Assert
import org.testng.annotations.Test

class LangTest {

    @Test
    @Throws(Exception::class)
    fun testTr() {
        Assert.assertEquals(Lang.tr("todo"), "Todo")
    }

    @Test
    @Throws(Exception::class)
    fun testTrAlt() {
        Assert.assertEquals(Lang.tr("validate.invalid-alt", "dog"), "The dog is invalid")
    }

    @Test
    @Throws(Exception::class)
    fun testTrAlt2() {
        Assert.assertEquals(Lang.tr("validate.invalid", "dog"), "The field is invalid")
    }

    @Test
    @Throws(Exception::class)
    fun testTrAll() {
        Assert.assertEquals(Lang.trAll("username", "email", "apple"), arrayOf("Username", "Email", "apple"))
    }

    @Test
    @Throws(Exception::class)
    fun testTrAll2() {
        Assert.assertEquals(Lang.tr("validate.invalid-complex", "username", "perfect"), "The Username is perfect")
    }
}