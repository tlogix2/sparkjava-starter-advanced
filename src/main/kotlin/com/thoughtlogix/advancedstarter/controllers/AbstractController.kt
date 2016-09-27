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
import com.thoughtlogix.advancedstarter.db.JPA
import com.thoughtlogix.advancedstarter.db.PageParams
import com.thoughtlogix.advancedstarter.models.core.Log
import com.thoughtlogix.advancedstarter.models.users.User
import com.thoughtlogix.advancedstarter.server.ContextModel
import com.thoughtlogix.advancedstarter.server.Flash
import com.thoughtlogix.advancedstarter.services.db.LogDbService
import com.thoughtlogix.advancedstarter.services.db.UserDbService
import com.thoughtlogix.advancedstarter.utils.Crypto
import eu.bitwalker.useragentutils.UserAgent
import org.slf4j.LoggerFactory
import spark.Request
import spark.Response
import spark.Spark.*
import spark.utils.IOUtils
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

open class AbstractController @JvmOverloads constructor(protected var jpa: JPA? = null) {

    open val logger = LoggerFactory.getLogger(AbstractController::class.java)
    var flash = Flash
    protected var basePath = ""
    protected var model = ContextModel()
    protected var logService: LogDbService

    init {
        logService = LogDbService(jpa!!)

        /**
         * Sets a generic error handler so that the user doesn't get default white jetty error page
         */
        exception(Exception::class.java) { e, req, res ->
            logger.error(e.message, e)
            redirect(res, "/500")
        }
    }

    /**
     * Add before filters for public routes
     */
    fun initFilters(path: String) {
        initFilters(arrayOf(path))
    }

    /**
     * Add before filters for public routes
     */
    fun initFilters(paths: Array<String>) {
        for (path in paths) {
            before(path) { req, res ->

                if (shouldSkipInit(req)) return@before

                initContextModel(req)
                updateContextModel()
                setCorsHeaders(res)
                loadUser(req)
                createSessionId(req, res)
                setModule(req, res)
                setTitle()
                if (jpa != null) {
                    logRequest(req)
                }
            }
        }
    }

    /**
     * Add before filters for authenticated users
     */
    fun initCommonFilters(path: String) {
        val paths = arrayOf(path, path + "/*")
        initFilters(paths)
        startTransaction(paths)
        endTransaction(paths)
    }

    /**
     * Add before filters for authenticated users
     */
    fun initCommonFilters(path: String, role: String) {
        val paths = arrayOf(path, path + "/*")
        val roles = arrayOf(role)
        initFilters(paths)
        securityCheck(paths, roles)
        loadCommonDatabaseData(paths)
        startTransaction(paths)
        endTransaction(paths)
    }

    /**
     * Load data used when displaying common data so we dont need to make lots of db calls
     *
     * Todo: Replace this with a better caching system.
     */
    private fun loadCommonDatabaseData(paths: Array<String>) {
        for (path in paths) {
            before(path) { req, res ->
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
        model.put("embedded", isEmbeddedAgent(req))
        model.put("internal", isInternalAgent(req))
        model.put("commandModule", getCommandModule(req))
        model.put("commandAction", getCommandAction(req))
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
        val sessionId = getCookie(req, "sessionid") ?: getNewSessionId(res)
        model.put("sessionid", if (getCookie(req, "token") != null) getCookie(req, "token")!! else sessionId)
    }

    private fun getNewSessionId(res: Response): String {
        val sessionId = Crypto.generateMD5(Date().toString())
        setCookie(res, "sessionid", sessionId)
        return sessionId
    }

    /**
     * Load the user to be used thought this request lifecycle
     */
    private fun loadUser(req: Request) {
        if (model.get("user") == null && req.headers("Authorization") != null) {
            val service = UserDbService(jpa!!)
            val userAgent = UserAgent.parseUserAgentString(req.headers("User-Agent"))
            var token = req.headers("Authorization")
            if (token.contains("Token")) {
                token = token.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            }
            val user = service.getByAuthToken(token, req.ip(), userAgent.toString())
            if (user != null) {
                model.put("user", user)
                jpa!!.context.user = user
            }
        } else if (model.get("user") == null && getCookie(req, "token") != null) {
            val service = UserDbService(jpa!!)
            val userAgent = UserAgent.parseUserAgentString(req.headers("User-Agent"))
            val user = service.getByAuthToken(getCookie(req, "token"), req.ip(), userAgent.toString())
            if (user != null) {
                model.put("user", user)
                jpa!!.context.user = user
            }
        }
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
        return req.requestMethod().equals("options", ignoreCase = true) || req.pathInfo().contains("/universal/")
    }

    /**
     * Log request to special access log
     */
    private fun logRequest(req: Request) {
        val log = Log()
        log.session = model.get("sessionid") as String
        log.action = getCommandAction(req)
        log.module = getCommandModule(req)
        log.requestMethod = req.requestMethod()
        log.isAjax = false
        log.format = getFormat(req)
        log.params = req.pathInfo()
        log.domain = req.host()
        log.publicIp = req.ip()
        log.createdAt = Date()
        log.createdBy = if (model.get("user") != null) (model.get("user") as User).username else ""
        logService.save(log)
        logger.info(log.toString())
    }

    /**
     * Is the web browser embedded in a small panel within desktop app (i.e. search panel)
     */
    private fun isEmbeddedAgent(req: Request): String {
        return if (req.headers("User-Agent") != null && req.headers("User-Agent").contains("cnTrack-Embedded")) "true" else "false";
    }

    /**
     * Is the web browser running within one of the  apps
     */
    private fun isInternalAgent(req: Request): String {
        return if (req.headers("User-Agent") != null && req.headers("User-Agent").contains("cnTrack")) "true" else "false";
    }

    /**
     * Set page title
     */
    private fun setTitle() {
        //not used
    }

    /**
     * Sets the module for cntrack.  This affects colors and maybe minor features.
     */
    private fun setModule(req: Request, res: Response) {

        if (req.uri().equals("/")) {
            model.put("module", "analytics")
            return
        }

        if (getCookie(req, "module") != null) {
            model.put("module", getCookie(req, "module")!!)
            return
        }

        if (req.uri().contains("physicians")) {
            model.put("module", "physicians")
            setCookie(res, "module", "physicians")
        } else if (req.uri().contains("analytics")) {
            model.put("module", "analytics")
            //  setCookie(res, "module", "analytics")
        } else {
            model.put("module", "analytics")
        }
    }

    /**
     * Get the module (i.e controller) such as account, tracks, etc.
     */
    private fun getCommandModule(req: Request): String {
        val segments = getSegments(req)
        if (segments.size < 2) return ""

        return segments[1]
    }

    /**
     * Get the action (i.e routed method) such as edit, summary, build.
     */
    private fun getCommandAction(req: Request): String {
        val segments = getSegments(req)
        if (segments.size == 3)
            return segments[2]
        else if (segments.size == 4)
            return segments[3]
        else
            return ""
    }

    /**
     * Tokenize the url
     */
    private fun getSegments(req: Request): Array<String> {
        return req.uri().split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    }

    /**
     * Check if user has role to proceed
     */
    private fun securityCheck(path: String, role: String) {
        securityCheck(arrayOf(path), arrayOf(role))
    }

    /**
     * Check if user has role (from an array of roles) to proceed
     */
    private fun securityCheck(paths: Array<String>, roles: Array<String>) {
        for (path in paths) {
            before(path) { req, res ->

                if ("options".equals(req.requestMethod(), ignoreCase = true)) {
                    return@before
                }

                if (model.get("user") == null) {
                    if (isHtml(req)) {
                        redirect(res, "/login")
                    } else {
                        redirect(res, "/401")
                    }
                }

                val u = model.get("user") as User
                if (!u.hasRole(roles)) {
                    redirect(res, "/403")
                }
            }
        }
    }

    /**
     * Start transaction for the specified url path
     */
    fun startTransaction(path: String) {
        startTransaction(arrayOf(path))
    }

    /**
     * Start transaction for the specified url paths
     */
    fun startTransaction(paths: Array<String>) {
        for (path in paths) {
            before(path) { req, res ->

                /**
                 * @Todo: Hack to skip getHelpDocument.  Need to add more flexible filter.
                 */
                // If we skip gets we dont log to db
                // if ("get".equals(req.requestMethod(), ignoreCase = true)) {
                //    return@before;
                //}

                jpa!!.beginTransaction()
            }
        }
    }

    /**
     * End transaction for the specified route path
     */
    fun endTransaction(path: String) {
        endTransaction(arrayOf(path))
    }

    /**
     * End transaction for the specified route path
     */
    fun endTransaction(paths: Array<String>) {
        for (path in paths) {
            after(path) { req, res ->

                /**
                 * @Todo: Hack to skip get request.  Need to add more flexible filter.
                 */
                // If we skip gets we dont log to db
                // if ("get".equals(req.requestMethod(), ignoreCase = true)) {
                //    return@after;
                // }

                endTransaction()
            }
        }
    }

    /**
     * Download a file from a File object
     */
    @Throws(IOException::class)
    protected fun fileOut(res: Response, contentType: String, file: File, fileName: String) {
        res.raw().contentType = contentType
        res.raw().setHeader("Content-Disposition", String.format("attachment; filename=%s", fileName))
        res.raw().setContentLength(file.length().toInt())
        res.status(200)

        val os = res.raw().outputStream
        val `in` = FileInputStream(file)
        IOUtils.copy(`in`, os)
        `in`.close()
        os.close()
    }

    /**
     * Download a file from a input stream
     */
    @Throws(IOException::class)
    protected fun fileOut(res: Response, contentType: String, inputStream: InputStream, fileName: String) {
        res.raw().contentType = contentType
        res.raw().setHeader("Content-Disposition", String.format("attachment; filename=%s", fileName))
        res.status(200)

        val os = res.raw().outputStream
        IOUtils.copy(inputStream, os)
        inputStream.close()
        os.close()
    }

    /**
     * Wrapper for temp flash info message
     */
    protected fun flashInfo(req: Request, msg: String) {
        flash.addInfo((model.get("user") as User).authToken!!, msg)
    }

    /**
     * Wrapper for temp flash warning message
     */
    protected fun flashWarning(req: Request, msg: String) {
        flash.addWarning((model.get("user") as User).authToken!!, msg)
    }

    /**
     * Wrapper for temp flash error message
     */
    protected fun flashError(req: Request, msg: String) {
        flash.addError((model.get("user") as User).authToken!!, msg)
    }

        private val  EMPTY_STRING: String = ""

        /**
         * Parse page parameters for database use
         */
        fun getPageParams(req: Request, res: Response): PageParams {

            var page = 0
            if (req.queryParams("page") != null && !req.queryParams("page").equals("0", ignoreCase = true)) {
                page = Integer.parseInt(req.queryParams("page"))
                page--
            }

            val pageParams = PageParams()
            pageParams.page = page
            pageParams.size = if (req.queryParams("size") != null) Integer.parseInt(req.queryParams("size")) else 15
            pageParams.order = if (req.queryParams("order") != null) req.queryParams("order") else "id"
            pageParams.sort = if (req.queryParams("sort") != null) req.queryParams("sort") else "desc"
            pageParams.filter = if (req.queryParams("filter") != null) req.queryParams("filter") else ""
            pageParams.expression = if (req.queryParams("expression") != null) req.queryParams("expression") else ""

            pageParams.setLastOrder(getCookie(req, "order") ?: "id")
            pageParams.setLastSort(getCookie(req, "sort") ?: "desc")

            res.cookie("order", pageParams.order)
            res.cookie("sort", pageParams.sort)

            return pageParams
        }

        fun setCookie(res: Response, name: String, value: String) {
            //            res.cookie("/", name, value, -1, false, true)
            res.cookie(name, value)
        }

        fun getCookie(req: Request, name: String): String? {
            return req.cookie(name)
        }

        /**
         * Common end jpa transaction
         */
        fun endTransaction() {
            with(jpa!!) {
                try {
                    commitTransaction()
                } catch (ex: Exception) {
                    logger.error(ex.message, ex)
                    rollbackTransaction()
                    throw ex
                } finally {
                    closeEntityManager()
                }
            }

        }

        /**
         * Redirect to the specific path
         */
        fun redirect(res: Response, path: String): String {
            res.redirect(path)
            endTransaction()
            try {
                halt();
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

        /**
         * Is HTML request
         */
        fun isHtml(req: Request): Boolean {
            return getFormat(req).contains("html")
        }

        /**
         * Is JSON request
         */
        fun isJson(req: Request): Boolean {
            return getFormat(req).contains("json")
        }

        /**
         * Is XML request
         */
        fun isXml(req: Request): Boolean {
            return getFormat(req).contains("xml")
        }

}
