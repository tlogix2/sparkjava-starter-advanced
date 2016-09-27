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

package com.infoquant.gf.server.controllers.manager

import com.infoquant.gf.server.other.htmlOut
import com.infoquant.gf.server.other.parseInput
import com.infoquant.gf.server.other.serializedOut
import com.thoughtlogix.advancedstarter.Lang
import com.thoughtlogix.advancedstarter.controllers.AbstractController
import com.thoughtlogix.advancedstarter.db.JPA
import com.thoughtlogix.advancedstarter.db.PagedData
import com.thoughtlogix.advancedstarter.models.core.Model
import com.thoughtlogix.advancedstarter.services.Service
import spark.Request
import spark.Response
import spark.Spark.*
import java.util.*
import javax.validation.Validation
import javax.validation.Validator

open class ManagerController<T : Model>(protected var cls: Class<T>, jpa: JPA) : AbstractController(jpa) {
    var service: Service<T>? = null
    protected var objName: String = ""
    protected var className: String = ""
    protected var singularName: String = ""
    protected var pluralName: String = ""

    init {
        if (validator == null) {
            val factory = Validation.buildDefaultValidatorFactory()
            validator = factory.validator
        }
    }

    protected override fun updateContextModel() {
        model.put("cls", cls)
        model.put("objName", objName)
        model.put("singularName", singularName)
        model.put("pluralName", pluralName)
        model.put("basePath", basePath)
        model.put("className", className)
    }

    protected fun initCommonRoutes() {
        get(basePath, { req, res -> this.handleGetPage(req, res) })
        post(basePath, { req, res -> this.handleSave(req, res) })
        get(basePath + "/create", { req, res -> this.handleCreate(req, res) })
        get(basePath + "/:id", { req, res -> this.handleGet(req, res) })
        put(basePath + "/:id", { req, res -> this.handleSave(req, res) })
        post(basePath + "/:id", { req, res -> this.handleSave(req, res) })
        delete(basePath + "/:id", { req, res -> this.handleDelete(req, res) })
        get(basePath + "/:id/edit", { req, res -> this.handleEdit(req, res) })
        get(basePath + "/:id/delete", { req, res -> this.handleDelete(req, res) })
        get(basePath + "/:id/revisions", { req, res -> this.handleRevisions(req, res) })
        get(basePath + "/:id/revisions/:rev", { req, res -> this.handleRevision(req, res) })
    }

    protected fun initDialogRoutes() {
        get(basePath + "/selectlist", { req, res -> this.handleSelectList(req, res) })
        get(basePath + "/selectchoose", { req, res -> this.handleSelectChoose(req, res) })
        get(basePath + "/selectcreate", { req, res -> this.handleSelectCreate(req, res) })
        post(basePath + "/selectcreate", { req, res -> this.handleSelectSave(req, res) })
        get(basePath + "/:id/select", { req, res -> this.handleSelect(req, res) })
    }

    protected fun handleGetPage(req: Request, res: Response): String {
        val pagedData = service!!.getPagedData(getPageParams(req, res))
        preGetPageHook(req, res, pagedData)
        if (isHtml(req)) {
            model.put("pageParams", getPageParams(req, res))
            model.put("pagedData", pagedData)
            return htmlOut(model, "/manager/list.peb")
        } else {
            return serializedOut(pagedData, getFormat(req))
        }
    }

    protected fun handleCreate(req: Request, res: Response): String {
        var obj: T? = null
        try {
            obj = cls.newInstance()
        } catch (e: Exception) {
            logger.error(e.message, e)
        }

        preEditHook(req, res, obj)
        if (isHtml(req)) {
            model.put("item", obj!!)
            return htmlOut(model, "/manager/edit.peb")
        } else {
            return serializedOut(obj, getFormat(req))
        }
    }

    protected fun handleGet(req: Request, res: Response): String {
        var id: UUID? = null
        if (!req.params(":id").equals("0", ignoreCase = true)) {
            id = UUID.fromString(req.params(":id"))
        }
        var obj: T? = null
        if (id == null) {
            try {
                obj = cls.newInstance()
            } catch (e: Exception) {
            }

        } else {
            obj = service!!.getById(id)
        }

        preGetHook(req, res, obj)
        if (isHtml(req)) {
            model.put("item", obj!!)
            return htmlOut(model, "/manager/show.peb")
        } else {
            return serializedOut(obj, getFormat(req))
        }
    }

    protected fun handleEdit(req: Request, res: Response): String {
        val id = UUID.fromString(req.params(":id"))
        var obj: T? = null
        if (id == null) {
            try {
                obj = cls.newInstance()
            } catch (e: Exception) {
            }

        } else {
            obj = service!!.getById(id)
        }

        preEditHook(req, res, obj)
        if (isHtml(req)) {
            model.put("item", obj!!)
            return htmlOut(model, "/manager/edit.peb")
        } else {
            return serializedOut(obj, getFormat(req))
        }
    }

    protected fun handleSave(req: Request, res: Response): String {
        var obj = parseInput(req, cls, getFormat(req))

        val errors = validator!!.validate(obj)
        if (errors.size > 0) {
            model.put("errors", errors)
            if (isHtml(req)) {
                model.put("item", obj!!)
                return htmlOut(model, "/manager/edit.peb")
            } else {
                return serializedOut(obj, getFormat(req))
            }
        }

        preSaveHook(req, res, obj)
        service!!.save(obj!!)
        model.put("item", obj)
        if (isHtml(req)) {
            flashInfo(req, Lang.tr("crud.create.success", obj.toString()))
            return redirect(res, basePath + "/" + obj.getId())
        } else {
            return serializedOut(obj, getFormat(req))
        }
    }

    protected fun handleDelete(req: Request, res: Response): String {
        val id = req.params(":id")
        preDeleteHook(req, res, id)
        val result = service!!.delete(UUID.fromString(id))
        if (isHtml(req)) {
            flashInfo(req, Lang.tr("crud.delete.success"))
            return redirect(res, basePath)
        } else {
            return serializedOut(result, getFormat(req))
        }
    }

    protected fun handleRevisions(req: Request, res: Response): String {
        val revisions = service!!.getRevisions(UUID.fromString(req.params(":id")))
        if (isHtml(req)) {
            model.put("revisions", revisions)
            return htmlOut(model, "/manager/revisions.peb")
        } else {
            return serializedOut(revisions, getFormat(req))
        }
    }

    protected fun handleRevision(req: Request, res: Response): String {
        val revision = service!!.getRevision(UUID.fromString(req.params(":id")), Integer.parseInt(req.params(":rev")))
        if (isHtml(req)) {
            model.put("revision", revision!!)
            return htmlOut(model, "/manager/revision.peb")
        } else {
            return serializedOut(revision, getFormat(req))
        }
    }

    protected fun handleSelect(req: Request, res: Response): String {
        var id: UUID = UUID.fromString(req.params(":id"))
        var obj = service!!.getById(id)
        redirect404IfNull(res, obj)
        preSelectHook(req, res, obj)
        model.put("id", id.toString())
        model.put("closeWindow", true)
        return htmlOut(model, "/manager/select.peb")
    }

    protected fun handleSelectList(req: Request, res: Response): String {
        val pagedData = service!!.getPagedData(getPageParams(req, res))
        model.put("pageParams", getPageParams(req, res))
        if (isHtml(req)) {
            model.put("pagedData", pagedData)
            return htmlOut(model, "/manager/selectlist.peb")
        } else {
            return serializedOut(pagedData, getFormat(req))
        }
    }

    protected fun handleSelectCreate(req: Request, res: Response): String {
        var obj: T? = null
        try {
            obj = cls.newInstance()
        } catch (e: Exception) {
        }

        if (isHtml(req)) {
            model.put("item", obj!!)
            return htmlOut(model, "/manager/selectedit.peb")
        } else {
            return serializedOut(obj, getFormat(req))
        }
    }

    protected fun handleSelectSave(req: Request, res: Response): String {
        val obj = parseInput(req, cls, getFormat(req))
        val newObj = service!!.save(obj!!)
        model.put("item", newObj)
        if (isHtml(req)) {
            flashInfo(req, Lang.tr("crud.create.success", obj.toString()))
            return redirect(res, basePath + "/" + newObj.getId() + "/select")
        } else {
            return serializedOut(newObj, getFormat(req))
        }
    }

    protected fun handleSelectChoose(req: Request, res: Response): String {
        return htmlOut(model, "/manager/selectchoose.peb")
    }

    ////////////////////////////////////////////////////////////////////
    //
    // Hooks
    //
    // Easily add functionality without overriding the entire method
    // or passing in a lambda
    //
    ////////////////////////////////////////////////////////////////////
    protected open fun preGetPageHook(req: Request, res: Response, obj: PagedData<T>?) {
    }

    protected open fun preGetHook(req: Request, res: Response, obj: T?) {
    }

    protected open fun preSelectHook(req: Request, res: Response, obj: T?) {
    }

    protected open fun preEditHook(req: Request, res: Response, obj: T?) {
    }

    protected open fun preDeleteHook(req: Request, res: Response, obj: String) {
    }

    protected open fun preSaveHook(req: Request, res: Response, obj: T?) {
    }

    companion object {
        private var validator: Validator? = null
    }
}
