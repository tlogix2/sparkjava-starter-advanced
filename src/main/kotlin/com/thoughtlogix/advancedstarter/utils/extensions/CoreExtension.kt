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

package com.thoughtlogix.advancedstarter.utils.extensions

import com.thoughtlogix.advancedstarter.Lang
import com.thoughtlogix.advancedstarter.models.core.Model
import com.mitchellbosecke.pebble.extension.AbstractExtension
import com.mitchellbosecke.pebble.extension.Filter
import com.mitchellbosecke.pebble.extension.Function
import java.util.*

class CoreExtension : AbstractExtension() {

    override fun getFilters(): Map<String, Filter> {
        val filters = HashMap<String, Filter>()
        filters.put("tr", TrFilter())
        return filters
    }

    override fun getFunctions(): Map<String, Function> {
        val functions = HashMap<String, Function>()
        functions.put("existsIn", ExistsInFunction())
//        functions.put("isStarred", IsStarred())
//        functions.put("isLocked", IsLocked())
        return functions
    }

    inner class TrFilter : Filter {

        override fun getArgumentNames(): List<String>? {
            return null
        }

        override fun apply(input: Any?, args: Map<String, Any>): Any? {
            if (input == null) {
                return null
            }
            return Lang.tr(input as String)
        }
    }

    inner class ExistsInFunction : Function {

        override fun getArgumentNames(): List<String>? {
            val names = ArrayList<String>()
            names.add("id")
            names.add("models")
            return names
        }

        override fun execute(args: Map<String, Any>): Any? {
            if (args == null) {
                return false
            }
            val id = args["id"] as String
            val modelsSet = args["models"] as Set<Model>

            for (model in modelsSet.toList()) {
                if (id.equals(model.getId())) {
                    return true
                }
            }

            return false;
        }
    }

//    inner class IsStarred : Function {
//
//        override fun getArgumentNames(): List<String>? {
//            val names = ArrayList<String>()
//            names.add("starredList")
//            names.add("model")
//            names.add("username")
//            return names
//        }
//
//        override fun execute(args: Map<String, Any>): Any? {
//            if (args == null || args["starredList"] == null || args["model"] == null || args["username"] == null) {
//                return false
//            }
//            val starredList = args["starredList"] as List<Starred>
//            val model = args["model"] as Model
//            val username = args["username"] as String
//
//            return Starred.isStarred(starredList, model, username)
//        }
//    }
//
//    inner class IsLocked : Function {
//
//        override fun getArgumentNames(): List<String>? {
//            val names = ArrayList<String>()
//            names.add("lockedList")
//            names.add("model")
//            return names
//        }
//
//        override fun execute(args: Map<String, Any>): Any? {
//            if (args == null || args["lockedList"] == null || args["model"] == null) {
//                return false
//            }
//            val lockedList = args["lockedList"] as List<SystemLock>
//            val model = args["model"] as Model
//
//            return SystemLock.isLocked(lockedList, model)
//        }
//    }
}
