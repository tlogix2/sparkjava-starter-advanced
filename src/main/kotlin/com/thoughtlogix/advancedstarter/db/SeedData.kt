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

package com.thoughtlogix.advancedstarter.db

import com.thoughtlogix.advancedstarter.models.tools.Todo
import com.thoughtlogix.advancedstarter.models.users.Role
import com.thoughtlogix.advancedstarter.models.users.Team
import com.thoughtlogix.advancedstarter.models.users.User
import com.thoughtlogix.advancedstarter.services.db.GenericDbService
import org.slf4j.LoggerFactory

class SeedData(val jpa: JPA) {

    val logger = LoggerFactory.getLogger(SeedData::class.java)

    fun loadData(includeFake: Boolean) {

        val count = (jpa.entityManager!!.createQuery("select count(u.id) from User u").singleResult as Long).toInt()
        if (count == 0) {
            loadSeedData()
            if (includeFake) {
                loadFakeData()
            }
        }
    }

    private fun loadSeedData() {

        logger.info("Importing Seed Data")

        var genericService: GenericDbService
        jpa?.beginTransaction()

        ////////////////////////////////////////////////////////////////////
        // Permissions
        ////////////////////////////////////////////////////////////////////
        //        logger.info("Importing Permissions...");
        //        genericService = new GenericService(jpa, Permission.class);
        //        Permission defaultPermission = new Permission();
        //        defaultPermission.setName("Default Permission");
        //        genericService.save(defaultPermission);

        ////////////////////////////////////////////////////////////////////
        // Roles
        ////////////////////////////////////////////////////////////////////
        logger.info("Importing Roles...")

        //
        // Generic
        //
        genericService = GenericDbService(jpa, Role::class.java)
        val adminRole = Role()
        adminRole.name = "Admin"
        genericService.save(adminRole)

        val userRole = Role()
        userRole.name = "User"
        genericService.save(userRole)


        ////////////////////////////////////////////////////////////////////
        // Teams
        ////////////////////////////////////////////////////////////////////
        logger.info("Importing Teams...")

        //
        // Default
        //
        genericService = GenericDbService(jpa, Team::class.java)
        val defaultTeam = Team()
        defaultTeam.name = "Default"
        defaultTeam.isDefaultDeny = false
        genericService.save(defaultTeam)

        ////////////////////////////////////////////////////////////////////
        // Users
        ////////////////////////////////////////////////////////////////////
        logger.info("Importing Users...")

        //
        // Defaults
        //

        val user = User()
        user.isActive = true
        user.username = "user"
        user.password = "user-pass"
        user.email = "support@example.com"
        user.roles = setOf(userRole)
        user.teams = setOf(defaultTeam)
        user.notes = "This is the default user."
        genericService.save(user)

        val adminUser = User()
        adminUser.isActive = true
        adminUser.username = "admin"
        adminUser.password = "admin-pass"
        adminUser.email = "support@example.com"
        adminUser.roles = setOf(userRole, adminRole)
        adminUser.teams = setOf(defaultTeam)
        adminUser.notes = "This is the admin user."
        genericService.save(adminUser)

        jpa?.commitTransaction()
    }

    @JvmOverloads fun loadFakeData(size: Int = 10, regionSize: Int = 200) {
        logger.info("Importing fake data")
        var genericService: GenericDbService

        jpa?.beginTransaction()

        logger.info("Importing Fake Data: ")

        genericService = GenericDbService(jpa, Todo::class.java)
        val todo1  = Todo()
        todo1.title  ="Walk the dogs"
        todo1.isComplete = true
        genericService.save(todo1)

        val todo2  = Todo()
        todo2.title  ="Fix the code"
        todo2.isComplete = false
        genericService.save(todo2)

        val todo3  = Todo()
        todo3.title  ="Exercise"
        todo3.isComplete = false
        genericService.save(todo3)

        val todo4  = Todo()
        todo4.title  ="Walk the dogs (again)"
        todo4.isComplete = false
        genericService.save(todo4)

        jpa?.commitTransaction()
    }
}
