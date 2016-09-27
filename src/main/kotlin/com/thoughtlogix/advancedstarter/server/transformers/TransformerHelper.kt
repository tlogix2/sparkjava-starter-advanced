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

package com.infoquant.gf.server.other

import com.fasterxml.jackson.core.JsonProcessingException
import com.thoughtlogix.advancedstarter.server.ContextModel
import com.thoughtlogix.advancedstarter.server.transformers.TransformerFactory
import spark.Request
import java.io.IOException

/**
 * Output the current context to the specified format
 */
fun out(model: ContextModel, format: String?, template: String?): String {

    if (model == null || format == null || template == null) {
        return ""
    }

    try {
        val transformer = TransformerFactory.create(format)
        return transformer!!.render(model.getModel(), template)
    } catch (e: JsonProcessingException) {
//        Logger.error(e)
        return ""
    }

}

/**
 * Output the current context using html templates
 */
fun htmlOut(model: ContextModel, template: String?): String {
    if (model == null || template == null) {
        return ""
    }

    try {
        val hmtlTransformer = TransformerFactory.create("html")
        return hmtlTransformer!!.render(model.getModel(), template)
    } catch (e: JsonProcessingException) {
//        Logger.error(e)
        return ""
    }

}

/**
 * Output the current context to the specified serialized format
 */
fun serializedOut(model: Any?, format: String?): String {
    if (model == null || format == null) {
        return ""
    }

    try {
        val transformer = TransformerFactory.create(format)
        return transformer!!.render(model, "")
    } catch (e: JsonProcessingException) {
//        Logger.error(e)
        return ""
    }

}

/**
 * Convert incoming http data into the specified class
 */
fun <T : Any> parseInput(data: Request, className: Class<T>, format: String): T? {
    try {
        val transformer = TransformerFactory.create(format)
        return transformer!!.read(data, className)
    } catch (e: IOException) {
//        Logger.error(e)
        return null
    }

}