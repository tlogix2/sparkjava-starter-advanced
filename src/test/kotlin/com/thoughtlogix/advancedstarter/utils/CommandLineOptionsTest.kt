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

package com.thoughtlogix.advancedstarter.utils

import com.beust.jcommander.JCommander
import com.thoughtlogix.advancedstarter.server.CommandLineOptions
import org.testng.Assert
import org.testng.annotations.Test

class CommandLineOptionsTest {

    @Test
    @Throws(Exception::class)
    fun testPassingArgs() {

        val args = arrayOf(
                "--server-port", "1010",
                "--server-static", "/happy",
                "--smtp-host", "smtp.google.com",
                "--smtp-port", "9999",
                "--smtp-username", "google",
                "--smtp-password", "adwords"
        )

        val options = CommandLineOptions()
        JCommander(options, *args)

        Assert.assertEquals(options.serverPort, "1010")
        Assert.assertEquals(options.serverStaticPath, "/happy")
        Assert.assertEquals(options.smtpHost, "smtp.google.com")
        Assert.assertEquals(options.smtpPort, "9999")
        Assert.assertEquals(options.smtpUsername, "google")
        Assert.assertEquals(options.smtpPassword, "adwords")
    }

    @Test
    @Throws(Exception::class)
    fun testPassingNoArgs() {

        val args = arrayOf("")

        val options = CommandLineOptions()
        JCommander(options, *args)

        Assert.assertEquals(options.serverPort, "7011")
        Assert.assertEquals(options.serverStaticPath, "/public")
        Assert.assertEquals(options.smtpHost, "")
        Assert.assertEquals(options.smtpPort, "")
        Assert.assertEquals(options.smtpUsername, "")
        Assert.assertEquals(options.smtpPassword, "")
    }
}