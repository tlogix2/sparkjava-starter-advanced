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

import com.infoquant.gf.server.other.htmlOut
import com.thoughtlogix.advancedstarter.db.JPA
import org.slf4j.LoggerFactory
import spark.Spark.get

class ErrorController(jpa: JPA) : AbstractController(jpa) {
    override val logger = LoggerFactory.getLogger(ErrorController::class.java)

    init {
        initFilters(arrayOf("/400", "/401", "/402", "/403", "/404", "/500"))

        get("/401") { req, res ->
            res.status(401)
            model.put("title", "401 Unauthorized")
            model.put("msg", "Sorry...access denied.")
            htmlOut(model, "/error.peb")
        }

        get("/403") { req, res ->
            res.status(403)
            model.put("title", "403 Forbidden")
            model.put("msg", "Sorry...access denied.")
            htmlOut(model, "/error.peb")
        }

        get("/404") { req, res ->
            res.status(404)
            model.put("title", "404 Not Found")
            model.put("msg", "Sorry...where it went, we know not.")
            htmlOut(model, "/error.peb")
        }

        get("/500") { req, res ->
            res.status(500)
            model.put("title", "500 Big Error")
            model.put("msg", "Something bad happened.  This error has been logged for review.")
            htmlOut(model, "/error.peb")
        }

        get("/*") { req, res ->
            if (!req.pathInfo().startsWith("/assets")) {
                return@get redirect(res, "/404")
            }
            return@get null
        }
    }
}