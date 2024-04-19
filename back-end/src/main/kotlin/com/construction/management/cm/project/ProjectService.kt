package com.construction.management.cm.project

import com.construction.management.cm.exceptionhandler.CustomException
import com.construction.management.cm.user.UserRepository
import com.construction.management.cm.user.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProjectService {

    @Autowired
    lateinit var repository: ProjectRepository

    @Autowired
    lateinit var userService: UserService


    fun projectExists(name: String, userId: Long) : Boolean {
        return repository.projectExists(user = userId, project = name.lowercase()) > 0
    }


    fun projectUuidExists(uuid: UUID): Boolean {
        return repository.projectUuidExists(uuid) > 0
    }

    fun addProject(name: String, userEmail:String): String {
        val newProject = Project()
        val user = userService.getUser(userEmail)
        newProject.name = name.split("\\W+".toRegex()).joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }
        newProject.projectManager = user!!
        if (projectExists(name = newProject.name, userId = user!!.id)) {
            throw CustomException("project-exists", null)
        }
        var projectId = UUID.randomUUID()
        while(true) {
            if(!projectUuidExists(projectId)) {
                break
            } else {
                projectId = UUID.randomUUID()
            }
        }
        newProject.projectId = projectId
        repository.save(newProject)
        return "Project created successfully"
    }

}