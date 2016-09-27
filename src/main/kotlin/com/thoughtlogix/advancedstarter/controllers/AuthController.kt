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

package com.infoquant.gf.server.controllers

import com.infoquant.gf.server.other.htmlOut
import com.infoquant.gf.server.other.out
import com.infoquant.gf.server.other.parseInput
import com.infoquant.gf.server.other.serializedOut
import com.thoughtlogix.advancedstarter.Lang
import com.thoughtlogix.advancedstarter.controllers.AbstractController
import com.thoughtlogix.advancedstarter.db.JPA
import com.thoughtlogix.advancedstarter.models.core.Profile
import com.thoughtlogix.advancedstarter.models.forms.ResetPasswordForm
import com.thoughtlogix.advancedstarter.models.forms.UserForm
import com.thoughtlogix.advancedstarter.models.users.User
import com.thoughtlogix.advancedstarter.services.db.ProfileDbService
import com.thoughtlogix.advancedstarter.services.db.UserDbService
import com.thoughtlogix.advancedstarter.utils.NumberUtils
import eu.bitwalker.useragentutils.UserAgent
import org.apache.tools.ant.taskdefs.email.Mailer
import org.w3c.tidy.Report
import spark.Spark.*
import java.util.*

class AuthController(jpa: JPA) : AbstractController(jpa) {

    init {

        initFilters(arrayOf("/auth/*", "/login/*", "/login", "/resetpassword", "/register"))

        get("/login") { req, res ->
            if (model.get("user") != null) redirect(res, "/")
            out(model, getFormat(req), "/public/login.peb")
        }

        post("/login") { req, res ->
            val service = UserDbService(jpa!!)
            jpa!!.beginTransaction()
            val userAgent = UserAgent.parseUserAgentString(req.headers("User-Agent"))
            val user = service.validate(req.queryParams("username"), req.queryParams("password"), req.ip(), userAgent.toString())
            jpa!!.commitTransaction()
            if (user == null) {
                flash.addError(model.get("sessionid") as String, Lang.tr("error.login.failed"))
                res.redirect("/login")
                halt()
            }
            setCookie(res, "token", user!!.authToken!!)
            redirect(res, "/")
        }

        get("/logout", { req, res ->
            jpa!!.beginTransaction()
            val userAgent = UserAgent.parseUserAgentString(req.headers("User-Agent"))
            val service = UserDbService(jpa!!)
            val user = service.getByAuthToken(getCookie(req, "token"), req.ip(), userAgent.toString())
            if (user != null) {
                req.attribute("user", null)
                res.removeCookie("token")
                jpa!!.context.user = User()
                user.authToken = null
            }
            jpa!!.commitTransaction()
            //flash.addInfo(model.get("sessionid") as String, Lang.tr("msg.user.loggedout"))
            redirect(res, "/")
        })

        post("/auth/login") { req, res ->
            val body = req.body()

            if (body.isNullOrBlank()) {
                redirect(res, "/404")
            }

            val userForm = parseInput(req, UserForm::class.java, getFormat(req))

            if (userForm == null) {
                redirect(res, "/401")
            }

            jpa!!.beginTransaction()
            val userAgent = UserAgent.parseUserAgentString(req.headers("User-Agent"))
            val service = UserDbService(jpa!!)
            val user = service.validate(userForm!!.username, userForm.password, req.ip(), userAgent.toString())
            jpa!!.commitTransaction()

            if (user == null) {
                redirect(res, "/404")
            }

            serializedOut(user, getFormat(req))
        }

        get("/auth/logout", { req, res ->
            jpa!!.beginTransaction()
            val userAgent = UserAgent.parseUserAgentString(req.headers("User-Agent"))
            val service = UserDbService(jpa!!)
            val user = service.getByAuthToken(getCookie(req, "token"), req.ip(), userAgent.toString())
            if (user != null) {
                req.attribute("user", null)
                res.removeCookie("token")
                jpa!!.context.user = User()
                user.authToken = null
            }
            jpa!!.commitTransaction()
            serializedOut(User(), getFormat(req))
        })

        get("/resetpassword") { req, res -> htmlOut(model, "/public/resetpassword.peb") }

        post("/resetpassword") { req, res ->
            val resetPasswordForm = parseInput(req, ResetPasswordForm::class.java, getFormat(req))
            val username = resetPasswordForm?.username ?: ""
            val email = resetPasswordForm?.email ?: ""


            if (username.isNullOrBlank() || email.isNullOrBlank()) {
                redirect(res, "/401")
            }

            jpa!!.beginTransaction()
            val service = UserDbService(jpa!!)
            val user = service.getByUsernameAndEmail(username, email)

            if (user == null) {
                res.redirect("/usernotfound")
                halt()
            }
            val password = arrayOf("guanine", "adenine", "cytosine", "thymine")
                    .get(Random().nextInt(4)) + NumberUtils.getRandomInt(0, 1000)
            user!!.password = password
            service.save(user)

//            val report = Report()
//            report.template = "email/txt/user-resetpassword"
//            report.data.put("password", password)
//            val mailer = Mailer()
//            mailer.send(Lang.tr("resetPasswordRequest"), report.compile())

            flash.addInfo(model.get("sessionid") as String, Lang.tr("msg.user.resetpassword"))

            redirect(res, "/")
        }

        get("/usernotfound") { req, res -> htmlOut(model, "/public/usernotfound.peb") }

        get("/register") { req, res -> htmlOut(model, "/public/register.peb") }

        post("/register") { req, res ->
            val profile = parseInput(req, Profile::class.java, getFormat(req))
            val user = parseInput(req, User::class.java, getFormat(req))

            val username = user!!.username
            val email = profile!!.email


            if (username.isNullOrBlank() || email.isNullOrBlank()) {
                redirect(res, "/401")
            }

            jpa!!.beginTransaction()
            val userService = UserDbService(jpa!!)
            val profileService = ProfileDbService(jpa!!)
            val dbUser = userService.getByUserName(username)

            if (dbUser != null) {
                res.redirect("/404")
                halt()
            }
            profileService.save(profile)
            user.profile = profile
            user.email = profile.email
            user.isActive = true;
            userService.save(user)

//            val report = Report()
//            report.template = "email/txt/user-welcome"
//            report.data.put("user", user)
//            val mailer = Mailer()
//            mailer.send(Lang.tr("welcomeTo") + product!!.name, report.compile())

            flash.addError(model.get("sessionid") as String, Lang.tr("msg.user.register"))

            jpa!!.commitTransaction()

            redirect(res, "/")
        }
    }
}
