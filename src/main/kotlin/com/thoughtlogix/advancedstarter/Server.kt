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

import com.beust.jcommander.JCommander
import com.infoquant.gf.server.controllers.AuthController
import com.thoughtlogix.advancedstarter.app.settings.Settings
import com.thoughtlogix.advancedstarter.controllers.ErrorController
import com.thoughtlogix.advancedstarter.controllers.MainController
import com.thoughtlogix.advancedstarter.controllers.TodoController
import com.thoughtlogix.advancedstarter.db.JPA
import com.thoughtlogix.advancedstarter.db.SeedData
import com.thoughtlogix.advancedstarter.server.CommandLineOptions
import com.thoughtlogix.advancedstarter.utils.Memory
import org.slf4j.LoggerFactory
import spark.Spark.*
import spark.debug.DebugScreen.enableDebugScreen
import spark.servlet.SparkApplication
import java.util.*

class Server : SparkApplication {
    val logger = LoggerFactory.getLogger(Server::class.java)
    private val settings: Settings = Settings();
    private var jpa: JPA? = null

    /**
     * Constructor to standalone deployment using embedded jetty web server.
     *
     * @param args Command line options
     */
    constructor(args: Array<String>) {
        initServer(args)
    }

    /**
     * Initialize the server via app server (not supported)
     *
     * @Todo: Test and fix why using app server didn't work.  I'm probably including wrong jars.  Grrr...
     */
    override fun init() {
        try {
            throw Exception("Running server using an application server is not supported yet")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun initServer(args: Array<String>) {
        settings.load()
        parseArgs(args);

        if (Server.isDevMode) {
            enableDebugScreen();
        }

        port(options.serverPort.toInt())
        if (Server.isDevMode) {
            externalStaticFileLocation("src/main/resources/public/")
        } else {
            staticFileLocation(options.serverStaticPath)
        }

        setupDatabase()
        initControllers();
        displayStartupMessage();
        displaySystemProperties()
    }

    private fun setupDatabase() {
        jpa = JPA(settings.systemSettings.databaseSettings)
        //Migrations.runAll(settings.getDatabaseSettings());
        val seedData = SeedData(jpa as JPA)
        seedData.loadData(true)
    }

    private fun displayStartupMessage() {
        logger.info("=============================================================")
        logger.info("Spark Advanced Starter Started")
        // @Todo: Add versioning
        //logger.info("Version: " + App.product?.version)
        logger.info("Date: " + Date().toString())
        logger.info("OS: " + System.getProperty("os.name"))
        logger.info("Initial Memory: " + Memory.used + "Mb")
        logger.info("=============================================================")
    }

    private fun displaySystemProperties() {
        val props = System.getProperties()
        val e = props.propertyNames()

        while (e.hasMoreElements()) {
            val key = e.nextElement() as String
            logger.info(String.format("%s = %s", key, props.getProperty(key)))
        }
    }

    private fun initControllers() {
        MainController(jpa!!)
        AuthController(jpa!!)
        TodoController(jpa!!)
        ErrorController(jpa!!)
    }

    private fun parseArgs(args: Array<String>) {
        val options = CommandLineOptions()
        JCommander(options, *args)
        Server.options = options
    }

    companion object {
        var isDevMode = true;
        var options: CommandLineOptions = CommandLineOptions();
    }
}
