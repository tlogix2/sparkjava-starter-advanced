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

package com.thoughtlogix.advancedstarter.controllers

import com.infoquant.gf.server.other.htmlOut
import com.thoughtlogix.advancedstarter.db.JPA
import org.slf4j.LoggerFactory
import spark.Spark.get

class MainController(jpa: JPA) : AbstractController(jpa) {
    override val logger = LoggerFactory.getLogger(MainController::class.java)

    init {
        basePath = "/"

        /**
         * Initialize spark before filters for all routes
         */
        initFilters(arrayOf(basePath, basePath + "*"))

        get(basePath + "/") { rq, rs ->
            model.put("pageTitle", "Spark Advanced Starter")
            htmlOut(model, basePath + "public/index.peb");
        }

        get(basePath + "/features") { rq, rs ->
            model.put("pageTitle", "Features")
            htmlOut(model, basePath + "public/features.peb");
        }

        get(basePath + "/about") { rq, rs ->
            model.put("pageTitle", "About Us")
            htmlOut(model, basePath + "public/about.peb");
        }

        get(basePath + "/contact") { rq, rs ->
            model.put("pageTitle", "Contact Us")
            htmlOut(model, basePath + "public/contact.peb");
        }
    }
}