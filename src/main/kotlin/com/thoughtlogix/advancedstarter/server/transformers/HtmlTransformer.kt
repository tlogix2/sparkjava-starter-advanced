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

package com.thoughtlogix.advancedstarter.server.transformers

import com.thoughtlogix.advancedstarter.Server
import com.thoughtlogix.advancedstarter.server.ContextModel
import com.thoughtlogix.advancedstarter.server.DefaultTemplateEngine
import org.apache.commons.beanutils.BeanUtils
import org.slf4j.LoggerFactory
import spark.Request
import java.io.IOException
import java.lang.reflect.InvocationTargetException

class HtmlTransformer : Transformer {
    val logger = LoggerFactory.getLogger(HtmlTransformer::class.java)
    private val templateEngine = DefaultTemplateEngine()

    override fun render(model: ContextModel, template: String): String {
        return templateEngine.render(model.getModel(), template)
    }

    @Throws(IOException::class)
    override fun <T : Any> read(req: Request, className: Class<T>): T? {
        try {
            val obj = className.newInstance()
            BeanUtils.populate(obj, req.queryMap(className.simpleName).toMap())
            return obj
        } catch (e: InstantiationException) {
            logger.error(e.message, e)
            return null
        } catch (e: IllegalAccessException) {
            logger.error(e.message, e)
            return null
        } catch (e: InvocationTargetException) {
            logger.error(e.message, e)
            return null
        }

    }
}
