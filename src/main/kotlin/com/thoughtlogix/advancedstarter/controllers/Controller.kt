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

import com.thoughtlogix.advancedstarter.Server
import com.thoughtlogix.advancedstarter.server.ContextModel
import com.thoughtlogix.advancedstarter.server.DefaultTemplateEngine
import com.thoughtlogix.advancedstarter.server.Flash
import org.slf4j.LoggerFactory
import spark.Request
import spark.Response
import spark.Spark
import java.util.*

open class Controller {
    open val logger = LoggerFactory.getLogger(Controller::class.java)
    private val EMPTY_STRING: String = "";
    private val templageEngine = DefaultTemplateEngine()

    protected var templatePath: String = ""
    protected var baseRoutePath: String = ""
    protected var model = ContextModel()
    var flash = Flash

    init {
        model.reset()

        /**
         * Sets a generic error handler so that the user doesn't get default white jetty error page
         * during production.
         */
        if (!Server.isDevMode) {
            Spark.exception(Exception::class.java) { e, req, res ->
                logger.error(e.message, e)
                redirect(res, "/500")
            }
        }
    }

    /**
     * Add basic filters for public routes
     */
    fun initFilters(path: String) {
        initFilters(arrayOf(path))
    }

    /**
     * Add basic filters for public routes
     */
    fun initFilters(paths: Array<String>) {
        for (path in paths) {
            Spark.before(path) { req, res ->

                if (shouldSkipInit(req)) return@before
                initContextModel(req)
                updateContextModel()
                setCorsHeaders(res)
                createSessionId(req, res)
                logRequest(req)
            }
        }
    }


    /**
     * Setup the default context model which hold most data for the request life cycle.  This will be
     * passed to the html templates for use.
     */
    private fun initContextModel(req: Request) {
        model.reset();
        //        model.put("req", req)
        //        model.put("res", res)
        model.put("flash", flash)
        model.put("commandMethod", req.requestMethod().toLowerCase())

        if (model.containsKey("errors")) {
            model.remove("errors")
        }
    }

    /**
     * Allow subclasses to add data to the main context model
     */
    protected open fun updateContextModel() {
    }

    /**
     * Use existing session id or create a new one.
     */
    private fun createSessionId(req: Request, res: Response) {
        //        val sessionId = getCookie(req, "sessionid") ?: getNewSessionId(res)
        //        model.put("sessionid", if (getCookie(req, "gfwid") != null) getCookie(req, "gfwid") else sessionId)
    }

    private fun getNewSessionId(res: Response): String {
        //        val sessionId = Crypto.generateMD5(Date().toString())
        //        setCookie(res, "sessionid", sessionId)
        //        return sessionId
        return ""
    }

    /**
     * Set the CORS headers for ajax requests (i.e. from the modern ui or desktop app)
     */
    private fun setCorsHeaders(res: Response) {
        res.header("Access-Control-Allow-Origin", "*")
        res.header("Access-Control-Allow-Headers", "Authorization, Origin, X-Requested-With, Content-Type, Accept, X-GN-AUTH-TOKEN")
    }

    /**
     * Skip the before route methods for simple things
     */
    private fun shouldSkipInit(req: Request): Boolean {
        return req.requestMethod().equals("options", ignoreCase = true) || req.pathInfo().contains("/assets/")
    }

    /**
     * Log request to special access log
     */
    private fun logRequest(req: Request) {
        val msg = String.format("%s|%s|%s|%s|%s|%s|%s",
                req.pathInfo(),
                req.requestMethod(),
                getFormat(req),
                req.host(),
                req.ip(),
                Date(),
                "" // @Todo: was session id & user
        );
        logger.info(msg)
    }


    /**
     * Render the output (in this case we only care about html format)
     */
    fun out(model: ContextModel, template: String): String {
        return templageEngine.render(model.getModel(), template)
    }

    /**
     * Redirect to the specific path
     */
    fun redirect(res: Response, path: String): String {
        res.redirect(path)
        try {
            Spark.halt();
        } catch(e: Exception) {
            //eat it
        }

        return EMPTY_STRING
    }

    /**
     * Redirect to the specific path if the object is null
     */
    fun redirectIfNull(res: Response, obj: Any?): String {
        if (obj == null) {
            redirect(res, "/404")
        }
        return EMPTY_STRING
    }

    /**
     * Redirect to 404 if the object is null
     */
    fun redirect404IfNull(res: Response, obj: Any?): String {
        if (obj == null) {
            redirect(res, "/404")
        }
        return EMPTY_STRING
    }

    /**
     * Return the format (i.e. json, html, xml) of the request.
     */
    fun getFormat(req: Request): String {
        val accept = req.headers("Accept") ?: return "html"

        if (accept.contains("html"))
            return "html"
        else if (accept.contains("xml"))
            return "xml"
        else if (accept.contains("json"))
            return "json"
        else
            return "html"
    }
}