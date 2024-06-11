package com.construction.management.cm.report

import com.construction.management.cm.Runner.Runner
import com.construction.management.cm.client.ClientRepository
import com.construction.management.cm.dto.*
import com.construction.management.cm.employee.Employee
import com.construction.management.cm.employee.EmployeeRepository
import com.construction.management.cm.employee.EmployeeService
import com.construction.management.cm.employeetype.EmployeeTypeRepository
import com.construction.management.cm.employeewagepayment.EmployeeWagePaymentRepository
import com.construction.management.cm.exceptionhandler.CustomException
import com.construction.management.cm.formatters.StringFormatter
import com.construction.management.cm.project.Project
import com.construction.management.cm.project.ProjectRepository
import com.construction.management.cm.project.ProjectService
import com.construction.management.cm.task.TaskRepository
import com.construction.management.cm.user.UserService
import net.sf.jasperreports.engine.*
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.util.*

@Service
class ReportsService (
    private val projectsRepository: ProjectRepository,
    private val userService: UserService,
    private val formatter: StringFormatter,
    private val runner: Runner,
    private val clientRepository: ClientRepository,
    private val projectService: ProjectService,
    private val employeeRepository: EmployeeRepository,
    private val employeeTypeRepository: EmployeeTypeRepository,
    private val employeeWagePaymentRepository: EmployeeWagePaymentRepository,
    private val employeeService: EmployeeService,
    private val taskRepository: TaskRepository) {


    private val calendar = Calendar.getInstance()
    private val currentYear = calendar.get(Calendar.YEAR)

    private val reportTemplatesDir = "C:\\software\\construction-management\\back-end\\src\\main\\resources\\templates\\"
    private val reportsOutputDir = "C:\\software\\construction-management\\back-end\\src\\main\\resources\\static\\"
    fun getDashboardReport(userEmail: String): ProjectReport {
        val userId: Long = userService.getUserId(userEmail)!!
        val userProjects = projectsRepository.getProjects(userId)
        var totalAvailableBudget = 0.0
        for (project in userProjects) {
            totalAvailableBudget += (project.totalBudgetAmountReceived - project.totalBudgetAmountSpent)
        }
        return ProjectReport(
            completeProjects = projectsRepository.getCompleteCount(userId),
            abandonedProjects = projectsRepository.getAbandonedCount(userId),
            ongoingProjects = projectsRepository.getOngoingCount(userId),
            totalAvailableBudget = formatter.doubleToString(totalAvailableBudget),
            projectBudgets = projectBudgetDbToProjectBudgetResponse(projectsRepository.getProjects(userId))
        )
    }

    fun projectBudgetDbToProjectBudgetResponse(projectBudgetDb: MutableSet<Project>): MutableList<ProjectBudgetResponse> {
        val result = mutableListOf<ProjectBudgetResponse>()
        for (project in projectBudgetDb) {
            result.add(
                ProjectBudgetResponse(
                    name = project.name,
                    budget = formatter.doubleToString(project.totalBudgetAmountReceived - project.totalBudgetAmountSpent)
                )
            )
        }
        return result
    }

    fun getGeneralReport(userEmail: String): String {

        val userId = userService.getUserId(userEmail)!!

        // Report Creation
        val numberOfProjectsCreated = NumberOfProjectsCreated(
            years = "${projectsRepository.getEarliestProjectYear(userId)} - ${
                projectsRepository.getLatestProjectYear(
                    userId
                )
            }",
            numberOfProjectsCreated = projectsRepository.getTotalNumberOfProjects(userId)
        )
        val projectsCreatedDataset = JRBeanCollectionDataSource(mutableSetOf(numberOfProjectsCreated))


        val projectStatusPieChart = mutableListOf<ProjectStatusPieChart>()
        projectStatusPieChart.addAll(
            listOf(
                ProjectStatusPieChart(
                    projectStatus = "Ongoing",
                    count = projectsRepository.getOngoingCount(userId)
                ),
                ProjectStatusPieChart(
                    projectStatus = "Complete",
                    count = projectsRepository.getCompleteCount(userId)
                ),
                ProjectStatusPieChart(
                    projectStatus = "Abandoned",
                    count = projectsRepository.getAbandonedCount(userId)
                ),
            )
        )
        val projectStatusPieChartDataset = JRBeanCollectionDataSource(projectStatusPieChart)

        val projectBudget = mutableListOf<ProjectBudgetTable>()
        val projects = projectsRepository.getProjects(userId)
        for (project in projects) {
            val totalCommittedAmount = clientRepository.getTotalProjectCommittedAmount(project.id)
            projectBudget.add(
                ProjectBudgetTable(
                    name = project.name,
                    status = project.status.lowercase()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                    totalAmountCommitted = formatter.doubleToStringCommaSeparated(totalCommittedAmount),
                    totalAmountReceived = formatter.doubleToStringCommaSeparated(project.totalBudgetAmountReceived),
                    totalAmountSpent = formatter.doubleToStringCommaSeparated(project.totalBudgetAmountSpent),
                    totalAmountAvailable = formatter.doubleToStringCommaSeparated(project.totalBudgetAmountReceived - project.totalBudgetAmountSpent)
                )
            )
        }
        val projectBudgetDataSet = JRBeanCollectionDataSource(projectBudget)

        val projectEmployees = mutableListOf<ProjectEmployeesTable>()
        for (project in projects) {
            projectEmployees.add(
                ProjectEmployeesTable(
                    name = project.name,
                    count = project.employees.size
                )
            )
        }
        val projectEmployeesDataSet = JRBeanCollectionDataSource(projectEmployees)


        val parameters = mutableMapOf<String, Any>()
        parameters["reportYear"] = currentYear.toString()
        parameters["projectStatusPieChartDataSet"] = projectStatusPieChartDataset
        parameters["projectEmployeeDataset"] = projectEmployeesDataSet
        parameters["numberOfProjectsCreated"] = projectsCreatedDataset
        parameters["projectBudgetBreakdown"] = projectBudgetDataSet

        generateJasperReportPdf (
            templatePath = "${reportTemplatesDir}general_report.jrxml",
            outputFilePath = "${reportsOutputDir}general-report.pdf",
            parameters = parameters
        )
        return pdfToString("${reportsOutputDir}general-report.pdf")
    }


    fun generateJasperReportPdf(templatePath: String, parameters: Map<String, Any>,
                                dataSource: JREmptyDataSource = JREmptyDataSource(),
                                outputFilePath: String) {
        val report: JasperReport = JasperCompileManager.compileReport(templatePath)
        val print: JasperPrint = JasperFillManager.fillReport(report, parameters, dataSource)
        JasperExportManager.exportReportToPdfFile(print, outputFilePath)
    }


    fun pdfToString(filePath: String): String {
        val generatedReport = File(filePath)
        val inputStream = FileInputStream(generatedReport)
        val byteArray = inputStream.readAllBytes()
        return Base64.getEncoder().encodeToString(byteArray)
    }

    fun getProjectGeneralReport(userEmail: String, project: Long): String {
        when (getProjectGeneralReportValidations(
            project = project,
            projectManager = userService.getUserId(userEmail)!!
        )) {
            "not-project-owner" -> throw CustomException("not-project-owner", null)
        }

        val fetchedProject = projectsRepository.findById(project).get()

        val projectInfo = mutableListOf<ProjectInfoTable>()
        projectInfo.add(
            ProjectInfoTable(
                creationDate = formatter.timestampToString(fetchedProject.creationDate),
                completionDate = when(fetchedProject.completionDate) {
                    null -> "n/a"
                    else -> formatter.timestampToString(fetchedProject.completionDate!!)
                },
                numberOfClients = fetchedProject.clients.size,
                numberOfEmployees = fetchedProject.employees.size,
                status = fetchedProject.status.lowercase()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            )
        )
        val projectInfoDataset = JRBeanCollectionDataSource(projectInfo)

        val parameters = mutableMapOf<String, Any>()
        parameters["reportYear"] = currentYear.toString()
        parameters["projectName"] = fetchedProject.name
        parameters["projectInfoDataset"] = projectInfoDataset

        generateJasperReportPdf (
            templatePath = "${reportTemplatesDir}general_project_report.jrxml",
            outputFilePath = "${reportsOutputDir}${fetchedProject.id}-general-report.pdf",
            parameters = parameters
        )
        return pdfToString("${reportsOutputDir}${fetchedProject.id}-general-report.pdf")
    }

    fun getProjectGeneralReportValidations (
        project: Long,
        projectManager: Long
    ): String {
        if(!projectService.isProjectManager(projectManager = projectManager, project = project)) {
            return "not-project-owner"
        }
        return "ok"
    }

    fun getProjectClientReport(userEmail: String, project: Long): String {
        when (getProjectGeneralReportValidations(
            project = project,
            projectManager = userService.getUserId(userEmail)!!
        )) {
            "not-project-owner" -> throw CustomException("not-project-owner", null)
        }

        val clientInfoTable = mutableSetOf<ClientInfoTable>()
        clientInfoTable.add(
            ClientInfoTable(
                numberOfClients = clientRepository.numberOfClientsInProject(project).toString(),
                totalAmountExpected = formatter.doubleToStringCommaSeparated(clientRepository.getTotalProjectCommittedAmount(project)),
                totalAmountReceived = formatter.doubleToStringCommaSeparated(clientRepository.getTotalProjectReceivedAmount(project))
            )
        )
        val clientInfoDataset = JRBeanCollectionDataSource(clientInfoTable)

        val clientAmountBreakDown = mutableSetOf<ClientAmountBreakdownTable>()
        val clients = clientRepository.getProjectClients(project)
        for (client in clients) {
            clientAmountBreakDown.add(
                ClientAmountBreakdownTable(
                    clientName = client.name,
                    totalExpected = formatter.doubleToStringCommaSeparated(client.committedAmount),
                    totalReceived = formatter.doubleToStringCommaSeparated(client.investedAmount)
                )
            )
        }
        val clientAmountDataset = JRBeanCollectionDataSource(clientAmountBreakDown)

        val parameters = mutableMapOf<String, Any>()
        parameters["reportYear"] = currentYear.toString()
        parameters["clientInfoDataset"] = clientInfoDataset
        parameters["clientAmountsDataset"] = clientAmountDataset


        generateJasperReportPdf (
            templatePath = "${reportTemplatesDir}client_report.jrxml",
            outputFilePath = "${reportsOutputDir}client-report.pdf",
            parameters = parameters
        )
        return pdfToString("${reportsOutputDir}client-report.pdf")



    }

    fun getProjectEmployeeReport(userEmail: String, project: Long): String {
        when (getProjectGeneralReportValidations(
            project = project,
            projectManager = userService.getUserId(userEmail)!!
        )) {
            "not-project-owner" -> throw CustomException("not-project-owner", null)
        }

        val employeeCountTable = mutableSetOf<EmployeeCountTable>()
        employeeCountTable.add(EmployeeCountTable(numberOfEmployees = employeeRepository.numberOfEmployeesInProject(project).toString()))
        val employeeCountDataset = JRBeanCollectionDataSource(employeeCountTable)

        val employeeTypeTable = mutableSetOf<EmployeeTypeTable>()
        val employeeTypes = employeeTypeRepository.getUserEmployeeTypes(userService.getUserId(userEmail)!!)
        for (type in employeeTypes) {
            if (type.employees.size > 0) {
                employeeTypeTable.add(
                    EmployeeTypeTable(
                    employeeType = type.name,
                    employeeCount = type.employees.size.toString()
                )
                )
            }
        }
        val employeeTypesDataset = JRBeanCollectionDataSource(employeeTypeTable)


        val wagesTable = mutableSetOf<WagesTable>()
        val employees = employeeRepository.getProjectEmployees(project)
        var paidWages = 0.0
        var unpaidWages = 0.0
        for (employee in employees) {
            paidWages += employeeWagePaymentRepository.employeeWagesPaid(employee.id)
            unpaidWages += employeeService.getGeneratedPay(
                employeeId = employee.id,
                userEmail = userEmail,
                project = project
            ).amountToPay.toDouble()
        }
        wagesTable.add(
            WagesTable(
               paidWages = formatter.doubleToStringCommaSeparated(paidWages),
               unpaidWages = formatter.doubleToStringCommaSeparated(unpaidWages),
               estimatedBudget = formatter.doubleToStringCommaSeparated(projectsRepository.getProjectBudgetAvailable(project) - unpaidWages)
            )
        )
        val wagesTableDataset = JRBeanCollectionDataSource(wagesTable)

        val employeePerformanceTable = mutableSetOf<EmployeePerformanceTable>()
        if (employees.size < 2) {
            employeePerformanceTable.add(
                EmployeePerformanceTable(
                    rank = "Best Employee",
                    employeeType = "n/a",
                    totalTasks = "n/a",
                    closedTasks = "n/a",
                    openTasks = "n/a",
                    averageCompletionTime = "n/a",
                    employeeName = "n/a"
                )
            )
        } else {
            var bestEmployee: Employee? = when(employeeRepository.getEmployeeDoneTasks(employees[0].id)) {
                0 -> null
                else -> employees[0]
            }
            var bestEmployeeScore = when(employeeRepository.getEmployeeDoneTasks(employees[0].id)) {
                0 -> null
                else -> (employeeRepository.getEmployeeDoneTasks(bestEmployee!!.id)
                        / employeeRepository.getEmployeeAllTasks(bestEmployee.id) ) * (1/employeeRepository.getAverageTasksCompletionTime(bestEmployee.id))
            }

            for (employee in employees) {
                if (employeeRepository.getEmployeeDoneTasks(employee.id) == 0 || employeeRepository.getEmployeeAllTasks(employee.id) == 0) {
                    continue
                } else {
                    val score = (employeeRepository.getEmployeeDoneTasks(employee.id)
                            / employeeRepository.getEmployeeAllTasks(employee.id)) * (1/employeeRepository.getAverageTasksCompletionTime(employee.id))
                    if (bestEmployeeScore == null || score > bestEmployeeScore) {
                        bestEmployee = employee
                        bestEmployeeScore = score
                    }
                }
            }
            if (bestEmployee == null) {
                employeePerformanceTable.add(
                    EmployeePerformanceTable(
                        rank = "Best Employee",
                        employeeType = "n/a",
                        totalTasks = "n/a",
                        closedTasks = "n/a",
                        openTasks = "n/a",
                        averageCompletionTime = "n/a",
                        employeeName = "n/a"
                    )
                )
            } else {
                employeePerformanceTable.add(
                    EmployeePerformanceTable(
                        rank = "Best Employee",
                        employeeType = bestEmployee.employeeType.name,
                        totalTasks = employeeRepository.getEmployeeAllTasks(bestEmployee.id).toString(),
                        closedTasks = employeeRepository.getEmployeeDoneTasks(bestEmployee.id).toString(),
                        openTasks = employeeRepository.getEmployeeActiveTasks(bestEmployee.id).toString(),
                        averageCompletionTime = employeeRepository.getAverageTasksCompletionTime(bestEmployee.id).toString(),
                        employeeName = "${bestEmployee.firstName.capitalize()} ${bestEmployee.lastName.capitalize()}"
                    )
                )
            }
        }
        val employeePerformanceTableDataset = JRBeanCollectionDataSource(employeePerformanceTable)

        val parameters = mutableMapOf<String, Any>()
        parameters["reportYear"] = currentYear.toString()
        parameters["numberOfEmployeesDataset"] = employeeCountDataset
        parameters["employeeTypeDataset"] = employeeTypesDataset
        parameters["wagesDataset"] = wagesTableDataset
        parameters["employeePerformanceDataset"] = employeePerformanceTableDataset


        generateJasperReportPdf (
            templatePath = "${reportTemplatesDir}employee_report.jrxml",
            outputFilePath = "${reportsOutputDir}${project}-employee-report.pdf",
            parameters = parameters
        )
        return pdfToString("${reportsOutputDir}${project}-employee-report.pdf")



    }

    fun getProjectTaskReport(userEmail: String, project: Long): String {
        when (getProjectGeneralReportValidations(
            project = project,
            projectManager = userService.getUserId(userEmail)!!
        )) {
            "not-project-owner" -> throw CustomException("not-project-owner", null)
        }

        val taskInfoTable = mutableSetOf<TaskInfoDataset>()
        taskInfoTable.add(
            TaskInfoDataset(
                total = taskRepository.getProjectTasks(project).size.toString(),
                todo = taskRepository.getProjectToDoTasks(project).toString(),
                inProgress = taskRepository.getProjectInProgressTasks(project).toString(),
                done = taskRepository.getProjectDoneTasks(project).toString()
            )
        )
        val taskInfoDataset = JRBeanCollectionDataSource(taskInfoTable)

        val taskCompletionTable = mutableSetOf<TaskCompletionTable>()
        taskCompletionTable.addAll(
            listOf(
                TaskCompletionTable(
                    priority = "All",
                    averageCompletionTime = formatter.doubleToStringCommaSeparated(taskRepository.getProjectAverageTaskCompletionTime(project))
                ),
                TaskCompletionTable(
                    priority = "Low",
                    averageCompletionTime = formatter.doubleToStringCommaSeparated(taskRepository.getProjectAverageLowPriorityTaskCompletionTime(project))
                ),
                TaskCompletionTable(
                    priority = "Medium",
                    averageCompletionTime = formatter.doubleToStringCommaSeparated(taskRepository.getProjectAverageMediumPriorityTaskCompletionTime(project))
                ),
                TaskCompletionTable(
                    priority = "High",
                    averageCompletionTime = formatter.doubleToStringCommaSeparated(taskRepository.getProjectAverageHighPriorityTaskCompletionTime(project))
                ),
            )
        )
        val taskCompletionDataset = JRBeanCollectionDataSource(taskCompletionTable)



        val parameters = mutableMapOf<String, Any>()
        parameters["reportYear"] = currentYear.toString()
        parameters["taskInformationDataset"] = taskInfoDataset
        parameters["taskCompletionDataset"] = taskCompletionDataset

        generateJasperReportPdf (
            templatePath = "${reportTemplatesDir}task_report.jrxml",
            outputFilePath = "${reportsOutputDir}${project}-task-report.pdf",
            parameters = parameters
        )
        return pdfToString("${reportsOutputDir}${project}-task-report.pdf")

    }


}
