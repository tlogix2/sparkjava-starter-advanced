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

import com.thoughtlogix.advancedstarter.models.constants.FlashMessageType
import com.thoughtlogix.advancedstarter.server.Flash
import org.testng.Assert
import org.testng.annotations.Test

class FlashTest {
    val sessionId = "101929292929292"
    val sampleMessage = "How now brown cow"

    @Test
    fun testGetMessage() {

        val flash = Flash
        flash.addInfo(sessionId, sampleMessage)
        Assert.assertEquals(flash.getMessage(sessionId), sampleMessage)
    }

    @Test
    fun testGetType() {
        val flash = Flash
        flash.addError(sessionId, sampleMessage)
        Assert.assertEquals(flash.getType(sessionId), FlashMessageType.ERROR)
    }

    @Test
    fun testIsInfo() {
        val flash = Flash
        flash.addInfo(sessionId, sampleMessage)
        Assert.assertEquals(flash.getType(sessionId), FlashMessageType.INFO)
    }

    @Test
    fun testIsWarning() {
        val flash = Flash
        flash.addWarning(sessionId, sampleMessage)
        Assert.assertEquals(flash.getType(sessionId), FlashMessageType.WARNING)
    }

    @Test
    fun testIsError() {
        val sampleMessage = "How now brown cow"
        val flash = Flash
        flash.addError(sessionId, sampleMessage)
        Assert.assertEquals(flash.getType(sessionId), FlashMessageType.ERROR)
    }

    @Test
    fun testAddInfo() {
        val flash = Flash
        flash.addInfo(sessionId, sampleMessage)
        Assert.assertEquals(flash.getType(sessionId), FlashMessageType.INFO)
        Assert.assertEquals(flash.getMessage(sessionId), sampleMessage)
    }

    @Test
    fun testAddWarning() {
        val flash = Flash
        flash.addWarning(sessionId, sampleMessage)
        Assert.assertEquals(flash.getType(sessionId), FlashMessageType.WARNING)
        Assert.assertEquals(flash.getMessage(sessionId), sampleMessage)
    }

    @Test
    fun testAddError() {
        val flash = Flash
        flash.addError(sessionId, sampleMessage)
        Assert.assertEquals(flash.getType(sessionId), FlashMessageType.ERROR)
        Assert.assertEquals(flash.getMessage(sessionId), sampleMessage)
    }

}