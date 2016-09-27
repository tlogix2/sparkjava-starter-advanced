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

package com.thoughtlogix.advancedstarter.controllers

import com.thoughtlogix.advancedstarter.Server
import org.assertj.core.api.Assertions.assertThat
import org.fluentlenium.adapter.FluentTestNg
import org.fluentlenium.adapter.util.SharedDriver
import org.openqa.selenium.WebDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

@SharedDriver(type = SharedDriver.SharedType.PER_CLASS)
class MainControllerTest : FluentTestNg() {


    // Cant use groups.  Seems a bug in FluentTestNg.
    // Todo: Upgrade all testing dependencies
    // @BeforeClass(groups = arrayOf("integration"))
    @BeforeClass
    @Throws(Exception::class)
    fun setUp() {
        webDriver = HtmlUnitDriver()
        server = Server(arrayOf(""))
    }

    // @Test(groups = arrayOf("integration"))
    @Test
    fun testMainPagesExistTest() {
        goTo("http://localhost:7011/")
        assertThat(pageSource()).doesNotContain("404")
        assertThat(pageSource()).doesNotContain("500")
        assertThat(title()).contains("Starter")

        goTo("http://localhost:7011/about")
        assertThat(pageSource()).doesNotContain("404")
        assertThat(pageSource()).doesNotContain("500")
        assertThat(title()).contains("About")

        goTo("http://localhost:7011/features")
        assertThat(pageSource()).doesNotContain("404")
        assertThat(pageSource()).doesNotContain("500")
        assertThat(title()).contains("Features")

        goTo("http://localhost:7011/contact")
        assertThat(pageSource()).doesNotContain("404")
        assertThat(pageSource()).doesNotContain("500")
        assertThat(title()).contains("Contact")
    }

    override fun getDefaultDriver(): WebDriver {
        return webDriver!!
    }

    companion object {
        var server: Server? = null
        var webDriver: WebDriver? = null
    }

}