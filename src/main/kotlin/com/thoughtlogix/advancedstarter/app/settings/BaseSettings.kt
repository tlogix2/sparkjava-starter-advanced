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

package com.thoughtlogix.advancedstarter.app.settings

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Paths

open class BaseSettings {

    @JsonIgnore
    val logger = LoggerFactory.getLogger(BaseSettings::class.java)

    @JsonIgnore
    var fileName: String = ""

    @JsonIgnore
    protected var baseConfigFolder = ".conf"

    fun save() {
        val mapper = ObjectMapper()
        mapper.enable(SerializationFeature.INDENT_OUTPUT)

        val f = File(baseConfigFolder)
        if (!f.exists()) {
            f.mkdirs()
        }

        try {
            FileWriter(baseConfigFolder + File.separator + fileName).use { file ->
                file.write(mapper.writeValueAsString(this))
                logger.info("Successfully saved: " + fileName)
            }
        } catch (e: IOException) {
            logger.error(e.message, e)
        }

    }

    fun load() {
        val mapper = ObjectMapper()
        try {
            val json = String(Files.readAllBytes(Paths.get(baseConfigFolder + File.separator + fileName)))
            mapper.readerForUpdating(this).readValue<Any>(json)
            logger.info("Successfully loaded: " + fileName)
        } catch (e: NoSuchFileException) {
            this.save()
            logger.error("Settings file not found [$fileName]...saving")
        } catch (e: IOException) {
            logger.error(e.message, e)
        }

    }

}
