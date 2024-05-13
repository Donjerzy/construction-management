package com.construction.management.cm.project

import com.construction.management.cm.Runner.Runner
import com.construction.management.cm.client.Client
import com.construction.management.cm.client.ClientService
import com.construction.management.cm.dto.*
import com.construction.management.cm.employee.Employee
import com.construction.management.cm.employee.EmployeeService
import com.construction.management.cm.employeetype.EmployeeTypeRepository
import com.construction.management.cm.exceptionhandler.CustomException
import com.construction.management.cm.formatters.StringFormatter
import com.construction.management.cm.task.TaskService
import com.construction.management.cm.user.UserService
import com.construction.management.cm.wagetype.WageTypeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProjectService(private val clientService: ClientService ,
                     private val employeeService: EmployeeService,
                     private val taskService: TaskService,
                     val stringFormatter: StringFormatter,
                     private val employeeTypeRepository: EmployeeTypeRepository,
                     private val wageTypeRepository: WageTypeRepository) {

    @Autowired
    lateinit var repository: ProjectRepository

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var runner: Runner


    fun projectExists(name: String, userId: Long) : Boolean {
        return repository.projectExists(user = userId, project = stringFormatter.formatNames(name).lowercase()) > 0
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
            budgetSpent = repository.getProjectBudgetSpent(project),
            projectBudgetReceived = repository.getProjectBudgetReceived(project)
        )
    }

    fun getProject(projectId: Long): Project? {
        return repository.findById(projectId).get()
    }

    fun getClients(email: String?, project: Long): MutableSet<ProjectClient> {
        return mapClientToProjectClient(repository.findById(project).get().clients)
    }

    fun mapClientToProjectClient(clients: MutableSet<Client>): MutableSet<ProjectClient> {
        val result = mutableSetOf<ProjectClient>()
        for (client in clients) {
            result.add(
                ProjectClient(
                id = client.id,
                committedAmount = String.format("%.0f", client.committedAmount),
                investedAmount = String.format("%.0f", client.investedAmount),
                name = client.name,
                type = client.type
            ))
        }
        return result
    }

    private fun modifyCommittedAmount(projectId: Long, amount:Double) {
        repository.modifyCommittedAmount(projectId = projectId, amount = amount)
    }

    private fun modifyInvestedAmount(projectId: Long, amount: Double) {
        repository.modifyInvestedAmount(projectId = projectId, amount = amount)
    }

    fun editClient(userEmail: String, client: EditClient): String {
        when(editClientValidations(client = client, userEmail= userEmail)) {
            "client-doesn't-exist" -> throw CustomException("client-doesn't-exist", null)
            "client-with-provided-name-exists" -> throw CustomException("client-with-provided-name-exists", null)
            "committed-amount-lz" -> throw CustomException("committed-amount-lz", null)
            "invested-amount-lz" -> throw CustomException("invested-amount-lz", null)
            "committed-invested-error" -> throw CustomException("committed-invested-error", null)
            "invalid-client-type" -> throw CustomException("invalid-client-type", null)
            else -> {
                val dataStoreClient = clientService.getClient(projectId = client.project, id = client.clientId)!!
                var project = repository.findById(client.project).get()
                when {
                    client.committedAmount < dataStoreClient.committedAmount -> {
                        project.committedBudgetAmount -= dataStoreClient.committedAmount - client.committedAmount
                        modifyCommittedAmount(projectId = client.project, amount = project.committedBudgetAmount)
                    }
                    client.committedAmount > dataStoreClient.committedAmount -> {
                        project.committedBudgetAmount += client.committedAmount - dataStoreClient.committedAmount
                        modifyCommittedAmount(projectId = client.project, amount = project.committedBudgetAmount)
                    }
                    client.investedAmount < dataStoreClient.investedAmount -> {
                        project.totalBudgetAmountReceived -= dataStoreClient.investedAmount - client.investedAmount
                        modifyInvestedAmount(projectId = client.project, amount = project.totalBudgetAmountReceived)
                    }
                    client.investedAmount > dataStoreClient.investedAmount -> {
                        project.totalBudgetAmountReceived += client.investedAmount - dataStoreClient.investedAmount
                        modifyInvestedAmount(projectId = client.project, amount = project.totalBudgetAmountReceived)
                    }
                }
                project = repository.findById(client.project).get()
                dataStoreClient.name = stringFormatter.formatNames(client.name).uppercase()
                dataStoreClient.type = when(client.type.lowercase()) {
                    "individual" -> "INDIVIDUAL"
                    "group" -> "GROUP"
                    "organisation" -> "ORGANISATION"
                    else -> throw CustomException("invalid-client-type",null)
                }
                dataStoreClient.committedAmount = client.committedAmount
                dataStoreClient.investedAmount = client.investedAmount
                dataStoreClient.project = project
                clientService.saveClient(dataStoreClient)
            }
        }
        return "Client Edited Successfully"
    }

    fun editClientValidations(client: EditClient, userEmail: String):String {
        val validClientTypes = listOf("individual", "group",  "organisation" )
        if(!clientService.clientExistsId(projectId = client.project,
                                         id = client.clientId)) {
            return "client-doesn't-exist"
        }
        if (clientService.clientExistsDifferentId(clientId = client.clientId , projectId = client.project, name = client.name)) {
            return "client-with-provided-name-exists"
        }
        if (client.type !in validClientTypes) {
            return "invalid-client-type"
        }
        if (client.committedAmount < 0) {
            return "committed-amount-lz"
        }
        if (client.investedAmount < 0) {
            return "invested-amount-lz"
        }
        if (client.committedAmount < client.investedAmount) {
            return "committed-invested-error"
        }
        return "validation-ok"
    }

    fun getEmployees(email: String, project: Long): MutableSet<Employees> {
        /**
         * Validations
         * Is requester project owner?
         */
        val validationResult = getEmployeesValidation(projectManager = userService.getUserId(email)!!,
                                                      project = project)
        when (validationResult) {
            "not-project-owner" -> throw CustomException("not-project-owner", null)
        }
        val employees = employeeService.getProjectEmployees(project = project)
        return mapEmployeesToGetEmployeesResponseFormat(employees = employees)
    }

    private fun mapEmployeesToGetEmployeesResponseFormat(employees: MutableList<Employee>): MutableSet<Employees> {
        val result = mutableSetOf<Employees>()
        for (employee in employees) {
            result.add(
                Employees(
                    id = employee.id,
                    name = "${employee.firstName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} ${employee.lastName.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }}",
                    employeeType = employee.employeeType.name,
                    wage = stringFormatter.doubleToString(employee.wage),
                    wageType = employee.wageType.name
                )
            )
        }
        return result
    }


    fun getEmployeesValidation(projectManager: Long, project: Long): String {
        if(!isProjectManager(projectManager = projectManager, project = project)) {
            return "not-project-owner"
        }
        return "ok"
    }

    fun getProjectEmployee(email: String, project: Long, employee: Long): GetEmployee {
        /**
         * Validations
         *  Is project Owner
         *  Is employee in project.
         */
        when(getProjectEmployeeValidations(
            projectManager = userService.getUserId(email)!!,
            employee = employee,
            project = project
        )) {
            "not-project-owner" -> throw CustomException("not-project-owner", null)
            "employee-not-in-project" -> throw CustomException("employee-not-in-project", null)
            else -> {
                val fetchedEmployee = employeeService.getEmployee(employee)
                val employeeTaskBreakdown = taskService.getEmployeeTasksBreakDown(employee = employee)
                return GetEmployee (
                    id = fetchedEmployee.id,
                    archetype = "n/a",
                    email = fetchedEmployee.email,
                    employeeType = fetchedEmployee.employeeType.name,
                    firstName = fetchedEmployee.firstName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                    lastName = fetchedEmployee.lastName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                    wage = stringFormatter.doubleToString(fetchedEmployee.wage),
                    wageType = fetchedEmployee.wageType.name,
                    tasksOngoing = employeeTaskBreakdown.ongoing,
                    tasksCompleted = employeeTaskBreakdown.done,
                    tasksCompletedOnTime = employeeTaskBreakdown.doneOnTime,
                    tasksCompletedPastTime = employeeTaskBreakdown.donePastTime,
                    totalTasks = employeeTaskBreakdown.done + employeeTaskBreakdown.ongoing,
                    joinDate = stringFormatter.timestampToString(fetchedEmployee.joinDate),
                    hasContract = if (fetchedEmployee.contract == null) "no" else "yes",
                    wagesPaid = stringFormatter.doubleToString(fetchedEmployee.wagesPaid)
                )
            }
        }
    }

    fun getProjectEmployeeValidations(projectManager: Long, project: Long, employee: Long): String {
        if (repository.isProjectManager(projectManager = projectManager, project = project) < 1 ) {
            return "not-project-owner"
        }
        if (!employeeService.employeeInProjectEmployeeId(project = project, employee = employee)) {
            return "employee-not-in-project"
        }
        return "ok"
    }






}