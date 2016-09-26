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

import com.beust.jcommander.Parameter

class CommandLineOptions {

    ////////////////////////////////////////////////////////////////////
    // General
    ////////////////////////////////////////////////////////////////////
    @Parameter(names = arrayOf("--help"), help = true)
    private val help: Boolean = false
    @Parameter(names = arrayOf("--dev"), description = "Dev mode", hidden = true, arity = 1)
    val dev = false

    ////////////////////////////////////////////////////////////////////
    // Server
    ////////////////////////////////////////////////////////////////////
    @Parameter(names = arrayOf("--server-host"), description = "Bind server to this host")
    val serverHost: String = "localhost"
    @Parameter(names = arrayOf("--server-port"), description = "Bind server to this port")
    val serverPort: String = "4567"
    @Parameter(names = arrayOf("--server-static"), description = "The path to static resources")
    val serverStaticPath: String = "/public"

    ////////////////////////////////////////////////////////////////////
    // Smtp Mailer
    ////////////////////////////////////////////////////////////////////
    @Parameter(names = arrayOf("--smtp-host"), description = "The SMTP hostname or IP")
    val smtpHost: String = ""
    @Parameter(names = arrayOf("--smtp-port"), description = "The SMTP server port")
    val smtpPort: String = ""
    @Parameter(names = arrayOf("--smtp-username"), description = "The SMTP username")
    val smtpUsername: String = ""
    @Parameter(names = arrayOf("--smtp-password"), description = "The SMTP password")
    val smtpPassword: String = ""

}
