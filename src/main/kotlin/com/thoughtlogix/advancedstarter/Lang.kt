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

package com.thoughtlogix.advancedstarter

import java.nio.charset.Charset
import java.util.*

object Lang {

    private var bundle: ResourceBundle

    init {
        bundle = ResourceBundle.getBundle("bundles.default", java.util.Locale("en", "US"))
    }

    /**
     * Translate single string
     */
    fun tr(key: String): String {
        try {
            return com.thoughtlogix.advancedstarter.Lang.lookupValueInResourceBundle(key);
        } catch (ex: Exception) {
            return key
        }

    }

    /**
     * Translate multiple string
     */
    fun trAll(vararg args: String): Array<String> {
        val translatedArgs = args as Array<String>

        for (i in args.indices) {
            val translation: String = tr(args[i])
            if (translation.isNotEmpty()) {
                translatedArgs[i] = translation
            } else {
                translatedArgs[i] = args[i]
            }
        }
        return translatedArgs
    }

    /**
     * Translate a string then format with translated value
     */
    fun tr(key: String, data: String): String {
        try {
            val str = lookupValueInResourceBundle(key);
            return str.format(lookupValueInResourceBundle(data))
        } catch (ex: Exception) {
            return key
        }
    }

    /**
     * Translate a string then format with multiple translated values
     */
    fun tr(key: String, vararg args: String): String {
        val translatedArgs: Array<String> = trAll(*args)
        try {
            val str = lookupValueInResourceBundle(key);
            return str.format(*translatedArgs)
        } catch (ex: Exception) {
            return key
        }
    }

    private fun lookupValueInResourceBundle(key: String): String {
        try {
            return String(bytes = bundle.getString(key).toByteArray(Charset.forName("ISO-8859-1")), charset = Charset.forName("UTF-8"))
        } catch(e: MissingResourceException) {
            return key
        }
    }
}
