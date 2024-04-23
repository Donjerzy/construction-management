package com.construction.management.cm.project

import com.construction.management.cm.Runner.Runner
import com.construction.management.cm.client.ClientService
import com.construction.management.cm.dto.Overview
import com.construction.management.cm.dto.Projects
import com.construction.management.cm.employee.EmployeeService
import com.construction.management.cm.exceptionhandler.CustomException
import com.construction.management.cm.task.TaskService
import com.construction.management.cm.user.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProjectService(private val clientService: ClientService ,
                     private val employeeService: EmployeeService,
                     private val taskService: TaskService) {

    @Autowired
    lateinit var repository: ProjectRepository

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var runner: Runner


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

    fun getProjects(userEmail: String): MutableSet<Projects> {
        val userId = userService.getUserId(email = userEmail)
        return mapProjectToProjects(repository.getProjects(userId!!))
    }

    fun mapProjectToProjects(projects: MutableSet<Project>): MutableSet<Projects> {
        val mappedProjects = mutableSetOf<Projects>()
        for (project in projects) {
            mappedProjects.add(
                Projects(
                    id = project.id,
                    name = project.name,
                    status = project.status
                )
            )
        }
        return mappedProjects
    }

    fun isProjectManager(project:Long, projectManager:Long):Boolean {
        return repository.isProjectManager(
            projectManager = projectManager,
            project = project
        ) > 0
    }

    fun getOverview(email: String?, project: Long): Overview {
        if (!isProjectManager(project = project, projectManager = userService.getUserId(email!!)!!)) {
            throw CustomException("not-project-owner", null)
        }
        return Overview(
            projectName = repository.findById(project).get().name,
            numberOfClients = clientService.getNumberOfClientsInProject(project),
            numberOfEmployees = employeeService.getNumberOfEmployeesInProject(project),
            numberOfTasksDone = taskService.getProjectTaskStatus(project).done,
            numberOfTasksOngoing = taskService.getProjectTaskStatus(project).ongoing,
            budgetAvailable = repository.getProjectBudgetAvailable(project),
            budgetSpent = repository.getProjectBudgetSpent(project)
        )
    }

}