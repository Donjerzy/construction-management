package com.construction.management.cm.employee

import com.construction.management.cm.Runner.Runner
import com.construction.management.cm.dto.*
import com.construction.management.cm.employeetype.EmployeeTypeRepository
import com.construction.management.cm.employeewagepayment.EmployeeWagePayment
import com.construction.management.cm.employeewagepayment.EmployeeWagePaymentRepository
import com.construction.management.cm.employeewagepayment.EmployeeWagePaymentService
import com.construction.management.cm.exceptionhandler.CustomException
import com.construction.management.cm.formatters.StringFormatter
import com.construction.management.cm.project.ProjectRepository
import com.construction.management.cm.response.PredictionResponse
import com.construction.management.cm.task.TaskRepository
import com.construction.management.cm.user.UserService
import com.construction.management.cm.validator.Validator
import com.construction.management.cm.wagetype.WageTypeRepository
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import org.springframework.web.client.postForObject
import org.springframework.web.multipart.MultipartFile
import java.awt.PageAttributes
import java.io.File
import java.io.FileInputStream
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.abs

@Service
class EmployeeService(private val repository: EmployeeRepository,
                      private val userService: UserService,
                      private val validator: Validator,
                      private val employeeTypeRepository: EmployeeTypeRepository,
                      private val wageTypeRepository: WageTypeRepository,
                      private val stringFormatter: StringFormatter,
                      private val projectRepository: ProjectRepository,
                      private val runner: Runner,
                      private val employeeWagePaymentService: EmployeeWagePaymentService,
                      private val employeeWagePaymentRepository: EmployeeWagePaymentRepository,
                      private val taskRepository: TaskRepository) {

    private val predictionApi = "http://127.0.0.1:8000/prediction-api/predict"

    fun getNumberOfEmployeesInProject(projectId:Long):Int {
        return repository.numberOfEmployeesInProject(projectId)
    }


    fun employeeInProject(employeeEmail: String, project: Long): Boolean {
        return repository.employeeInProject(project = project, employeeEmail = employeeEmail.lowercase()) > 0
    }

    fun employeeInProjectEmployeeId(project: Long, employee: Long): Boolean {
        return repository.employeeInProjectEmployeeId(project = project, employeeId = employee) > 0
    }

    fun getEmployee(employee: Long): Employee {
        return repository.findById(employee).get()
    }


    fun addEmployee(employee: AddEmployee, userEmail: String): String {
        /**
         * Validation List
         *
         * Is requester project owner
         * Individual Field Formats
         * Does Employee already exist in project
         */
        val validationResult = addEmployeeValidations(employee= employee, projectManager = userService.getUserId(userEmail)!!)
        when (validationResult) {
            "not-project-owner" -> throw CustomException("not-project-owner", null)
            "first-name-empty" -> throw CustomException("first-name-empty", null)
            "last-name-empty" -> throw CustomException("last-name-empty", null)
            "email-empty" -> throw CustomException("email-empty", null)
            "invalid-email" -> throw CustomException("invalid-email", null)
            "invalid-password" -> throw CustomException("invalid-password", null)
            "wage-lz" -> throw CustomException("wage-lz", null)
            "invalid-date" -> throw CustomException("invalid-date", null)
            "invalid-employee-type" -> throw CustomException("invalid-employee-type", null)
            "invalid-wage-type" -> throw CustomException("invalid-wage-type", null)
            "employee-already-in-project" -> throw CustomException("employee-already-in-project", null)
        }
        val newEmployee = Employee()
        newEmployee.firstName = stringFormatter.formatNames(employee.firstName).lowercase()
        newEmployee.lastName = stringFormatter.formatNames(employee.lastName).lowercase()
        newEmployee.email = employee.email.lowercase()
        newEmployee.joinDate = stringFormatter.toDate(employee.joinDate)
        newEmployee.wage = employee.wage
        newEmployee.employeeType = employeeTypeRepository.findById(employee.employeeType).get()
        newEmployee.project = projectRepository.findById(employee.project).get()
        newEmployee.wageType = wageTypeRepository.findById(employee.wageType).get()
        if (employee.contract == null) {
            repository.save(newEmployee)
        } else {
            newEmployee.contract = saveEmployeeContract(email = employee.email, contract = employee.contract,
                project = projectRepository.findById(employee.project).get().projectId)
            repository.save(newEmployee)
        }
        return "Employee Added Successfully"
    }

    fun saveEmployeeContract(email: String, contract: MultipartFile, project: UUID): String {
        val contractDirectoryPath = "contracts"
        val fileName = "${email.lowercase()}-$project-contract"
        val contractDirectory = File(contractDirectoryPath)
        if(!contractDirectory.exists()) {
            contractDirectory.mkdir()
        }
        val fileSaveLocation = File(contractDirectory.absolutePath, fileName)
        contract.transferTo(fileSaveLocation)
        return fileSaveLocation.absolutePath
    }

    fun addEmployeeValidations(employee: AddEmployee, projectManager: Long): String {
        if(projectRepository.isProjectManager(project = employee.project,
                projectManager = projectManager) <= 0) {
            runner.printOwner("The owner is $projectManager")
            return "not-project-owner"
        }
        if (employee.firstName.isEmpty()) {
            return "first-name-empty"
        }
        if (employee.lastName.isEmpty()) {
            return "last-name-empty"
        }
        if(employee.email.isEmpty()) {
            return "email-empty"
        }
        if (!validator.isValidEmail(employee.email)) {
            return "invalid-email"
        }
        if (employee.wage < 0) {
            return "wage-lz"
        }
        if (!validator.isValidDate(employee.joinDate)) {
            return "invalid-date"
        }
        if (employeeTypeRepository.exists(employee.employeeType) == 0){
            return "invalid-employee-type"
        }
        if (wageTypeRepository.exists(employee.wageType) == 0){
            return "invalid-wage-type"
        }
        if (employeeInProject(employeeEmail = employee.email, project = employee.project)) {
            return "employee-already-in-project"
        }
        return "ok"
    }


    fun getProjectEmployees(project: Long): MutableList<Employee> {
        return repository.getProjectEmployees(project = project)
    }

    fun addContract(employee: Long, userEmail: String, contract: MultipartFile): String {
        when (addContractValidations(employee = employee, projectOwner = userService.getUserId(userEmail)!!)) {
            "employee-doesn't-exist" -> throw CustomException("employee-doesn't-exist", null)
            "not-project-owner" -> throw CustomException("not-project-owner", null)
            "employee-already-has-contract" -> throw CustomException("employee-already-has-contract", null)
        }
        val fetchedEmployee = repository.findById(employee).get()
        val contractLocation = saveEmployeeContract(
            email = fetchedEmployee.email,
            contract = contract,
            project = fetchedEmployee.project.projectId
        )
        fetchedEmployee.contract = contractLocation
        repository.save(fetchedEmployee)
        return "Contract added successfully"
    }

    fun addContractValidations(employee: Long, projectOwner: Long) : String {
        if (!repository.findById(employee).isPresent) {
            return "employee-doesn't-exist"
        }
        val fetchedEmployee = repository.findById(employee).get()
        if (fetchedEmployee.project.projectManager.id != projectOwner) {
            return "not-project-owner"
        }
        if (fetchedEmployee.contract != null) {
            return "employee-already-has-contract"
        }
        return "ok"
    }

    fun getContractValidations(employee: Long, projectOwner: Long) : String {
        if (!repository.findById(employee).isPresent) {
            return "employee-doesn't-exist"
        }
        val fetchedEmployee = repository.findById(employee).get()
        if (fetchedEmployee.project.projectManager.id != projectOwner) {
            return "not-project-owner"
        }
        if (fetchedEmployee.contract == null) {
            return "employee-has-no-contract"
        }
        return "ok"
    }

    fun getContract(userEmail: String, employeeId: Long): String {
        when (getContractValidations(employee = employeeId, projectOwner = userService.getUserId(userEmail)!!)) {
            "employee-doesn't-exist" -> throw CustomException("employee-doesn't-exist", null)
            "not-project-owner" -> throw CustomException("not-project-owner", null)
            "employee-has-no-contract" -> throw CustomException("employee-has-no-contract", null)
        }
        val actualDocument = File(repository.findById(employeeId).get().contract!!)
        val inputStream = FileInputStream(actualDocument)
        val byteArray = inputStream.readAllBytes()
        return Base64.getEncoder().encodeToString(byteArray)
    }

    fun modifyName(userEmail: String?, name: ModifyName): String {
        when(modifyNameValidations(name = name, projectOwner = userService.getUserId(userEmail!!)!!)) {
            "invalid-first-name" -> throw CustomException("invalid-first-name", null)
            "employee-doesn't-exist" -> throw CustomException("employee-doesn't-exist", null)
            "not-project-owner" -> throw CustomException("not-project-owner", null)
            "invalid-last-name" -> throw CustomException("invalid-last-name", null)
        }
        val fetchedEmployee = repository.findById(name.employeeId).get()
        fetchedEmployee.firstName = name.firstName.lowercase()
        fetchedEmployee.lastName = name.lastName.lowercase()
        repository.save(fetchedEmployee)
        return "Name modified successfully"
    }

    fun modifyNameValidations(name: ModifyName, projectOwner: Long): String {
        if (!repository.findById(name.employeeId).isPresent) {
           return "employee-doesn't-exist"
        }
        val employee = repository.findById(name.employeeId).get()
        if (projectOwner != employee.project.projectManager.id) {
            return "not-project-owner"
        }
        if (name.firstName.isEmpty() || name.firstName.isBlank()) {
            return "invalid-first-name"
        }
        if (name.lastName.isEmpty() || name.lastName.isBlank()) {
            return "invalid-last-name"
        }
        if (name.lastName.length < 3) {
            return "invalid-last-name"
        }
        if (name.firstName.length < 3) {
            return "invalid-first-name"
        }
        return "ok"
    }

    fun modifyEmail(userEmail: String, email: ModifyEmail): String {
        when(modifyEmailValidations(email = email, projectOwner = userService.getUserId(userEmail)!!)) {
            "employee-doesn't-exist" -> throw CustomException("employee-doesn't-exist", null)
            "not-project-owner" -> throw CustomException("not-project-owner", null)
            "invalid-email" -> throw CustomException("invalid-email", null)
            "employee-already-in-project" -> throw CustomException("employee-already-in-project", null)
        }
        val fetchedEmployee = repository.findById(email.employeeId).get()
        fetchedEmployee.email = email.email.lowercase()
        repository.save(fetchedEmployee)
        return "Email modified successfully"
    }



    fun modifyEmailValidations(email: ModifyEmail, projectOwner: Long): String {
        if (!repository.findById(email.employeeId).isPresent) {
            return "employee-doesn't-exist"
        }
        val employee = repository.findById(email.employeeId).get()
        if (projectOwner != employee.project.projectManager.id) {
            return "not-project-owner"
        }
        if (!validator.isValidEmail(email.email)) {
            return "invalid-email"
        }
        if (employeeInProject(employeeEmail = email.email, project = employee.project.id)) {
            return "employee-already-in-project"
        }
        return "ok"
    }

//    fun modifyPassword(userEmail: String, modifyBody: ModifyPassword): String {
//        when (modifyPasswordValidations(projectManager = userService.getUserId(userEmail)!!, modifyBody = modifyBody) )  {
//            "employee-doesn't-exist" -> throw CustomException("employee-doesn't-exist", null)
//            "not-project-owner" -> throw CustomException("not-project-owner", null)
//            "invalid-password" -> throw CustomException("invalid-password", null)
//        }
//        val employee = repository.findById(modifyBody.employeeId).get()
//        employee.password = stringFormatter.toPassword(modifyBody.newPassword)
//        repository.save(employee)
//        return "Password reset successfully"
//    }

    private fun modifyPasswordValidations(projectManager: Long, modifyBody: ModifyPassword): String {
        if (!repository.findById(modifyBody.employeeId).isPresent) {
            return "employee-doesn't-exist"
        }
        val employee = repository.findById(modifyBody.employeeId).get()
        if (projectManager != employee.project.projectManager.id) {
            return "not-project-owner"
        }
        if (!validator.isValidPassword(modifyBody.newPassword)) {
            return "invalid-password"
        }
        return "ok"
    }

    fun getWageInformation(userEmail: String, employeeId: Long): WageInfo {
        when (getWageInfoValidations(projectManager = userService.getUserId(userEmail)!!,
            employeeId = employeeId
        )) {
            "employee-doesn't-exist" -> throw CustomException("employee-doesn't-exist", null)
            "not-project-owner" -> throw CustomException("not-project-owner", null)
        }
        val employee = repository.findById(employeeId).get()
        return WageInfo(
            wage = stringFormatter.doubleToString(employee.wage),
            numberOfDays = employee.wageType.period,
            type = employee.wageType.name,
            nextValidPaymentDate = when(employeeWagePaymentService.noPaymentsMade(employeeId)) {
                true -> stringFormatter.timestampToString(employee.joinDate)
                false -> employeeWagePaymentService.nextPaymentDate(employeeId)
            }
        )
    }

    fun getWageInfoValidations(projectManager: Long, employeeId: Long): String {
        if (!repository.findById(employeeId).isPresent) {
            return "employee-doesn't-exist"
        }
        val employee = repository.findById(employeeId).get()
        if (projectManager != employee.project.projectManager.id) {
            return "not-project-owner"
        }
        return "ok"
    }

    fun payEmployee(userEmail: String, payEmployeeBody: PayEmployeeBody): String {
        when(payEmployeeValidations(
            projectManager = userService.getUserId(userEmail)!!,
            payEmployeeBody = payEmployeeBody
        )) {
            "employee-doesn't-exist" -> throw CustomException("employee-doesn't-exist", null)
            "not-project-owner" -> throw CustomException("not-project-owner", null)
            "invalid-amount" -> throw CustomException("invalid-amount", null)
            "invalid-date-period" -> throw CustomException("invalid-date-period", null)
        }
        val employee = repository.findById(payEmployeeBody.employeeId).get()
        val project = projectRepository.findById(employee.project.id).get()
        employee.wagesPaid += payEmployeeBody.amount
        repository.save(employee)
        project.totalBudgetAmountSpent += payEmployeeBody.amount
        projectRepository.save(project)
        val transaction = EmployeeWagePayment()
        transaction.periodStart = payEmployeeBody.startDate
        transaction.periodEnd = payEmployeeBody.endDate
        transaction.amount = payEmployeeBody.amount
        transaction.employee = employee
        transaction.note = when {
            payEmployeeBody.note == null -> null
            payEmployeeBody.note.isEmpty() -> null
            payEmployeeBody.note.isBlank() -> null
            else -> payEmployeeBody.note
        }
        employeeWagePaymentRepository.save(transaction)
        return "Transaction Recorded Successfully"
    }

    fun payEmployeeValidations(projectManager: Long, payEmployeeBody: PayEmployeeBody): String {
        if (!repository.findById(payEmployeeBody.employeeId).isPresent) {
            return "employee-doesn't-exist"
        }
        val employee = repository.findById(payEmployeeBody.employeeId).get()
        if (projectManager != employee.project.projectManager.id) {
            return "not-project-owner"
        }
        if (payEmployeeBody.amount < 0) {
            return "invalid-amount"
        }
        if (
            payEmployeeBody.startDate.before(
                stringFormatter.toDate(employeeWagePaymentService.nextPaymentDate(employeeId = payEmployeeBody.employeeId)))
            ) {
            return "invalid-date-period"
        }
        if (payEmployeeBody.startDate.after(payEmployeeBody.endDate)) {
            return "invalid-date-period"
        }
        return "ok"
    }

    fun getWageHistory(userEmail: String, employeeId: Long): MutableList<GetWageHistory> {
        when (getWageHistoryValidations(
            projectManager = userService.getUserId(userEmail)!!,
            employee = employeeId
        )) {
            "employee-doesn't-exist" -> throw CustomException("employee-doesn't-exist", null)
            "not-project-owner" -> throw CustomException("not-project-owner", null)
        }
        val history = employeeWagePaymentRepository.getByEmployeeOrderPeriodAsc(employeeId)
        val result = mutableListOf<GetWageHistory>()
        for (entry in history) {
            result.add (
                GetWageHistory (
                    note = entry.note ?: "n/a",
                    amount = stringFormatter.doubleToString(entry.amount),
                    periodStart = stringFormatter.timestampToString(entry.periodStart),
                    periodEnd = stringFormatter.timestampToString(entry.periodEnd),
                    transactionDate = stringFormatter.timestampToString(entry.transactionDate)
                )
            )
        }
        return result
    }

    fun getWageHistoryValidations(projectManager: Long, employee: Long): String {
        if (!repository.findById(employee).isPresent) {
            return "employee-doesn't-exist"
        }
        val fetchedEmployee = repository.findById(employee).get()
        if (projectManager != fetchedEmployee.project.projectManager.id) {
            return "not-project-owner"
        }
        return "ok"
    }

    fun getSuggestedEmployees(employeeType: Long, userEmail: String, project: Long): MutableSet<String> {
        when (getSuggestedEmployeesValidation(
            projectManager = userService.getUserId(userEmail)!!,
            project = project,
            employeeType = employeeType
        )) {
            "not-project-owner" -> throw CustomException("not-project-owner", null)
            "invalid-employee-type" -> throw CustomException("invalid-employee-type", null)
        }
        val employees = repository.getProjectEmployeesByType(project = project, type = employeeType)
        if (employees.size == 0) {
            return mutableSetOf()
        }
        /**
         * Prediction logic
         * For each employee compute input features.
         *      Hit API. Get the minutes and add them to map
         * Sort map, add map to list O(n^2)
         * return list
         */
        val restTemplate = RestTemplate()
        val employeePredictedMinutes = mutableMapOf<String, Double>()
        val rankedEmployees = mutableSetOf<String>()
        var predictionError = false
        for (employee in employees) {
            if (repository.getEmployeeDoneTasks(employee.id) <= 0) {
                continue
            }
            val features = mutableListOf<MutableList<Any>>()
            features.add(
                mutableListOf(
                    repository.getEmployeeActiveTasks(employee.id),
                    repository.getAverageTasksCompletionTime(employee.id)
                )
            )

            val requestBody  = "{\"features\": [${features[0]}]}"

            //var response = PredictionResponse(httpStatus = 200, "", 0.0)
            var response: ResponseEntity<PredictionResponse> = ResponseEntity.status(200).body(
                PredictionResponse(
                httpStatus = 200,
                message = "",
                minutes = 0.0
                )
            )
            try {
                val headers = HttpHeaders()
                headers.contentType = MediaType.APPLICATION_JSON
                val request = HttpEntity(requestBody, headers)
                response = restTemplate.exchange(predictionApi, HttpMethod.POST, request, PredictionResponse::class.java)
            } catch (e: Exception) {
                predictionError = true
            }
            if (predictionError) {
                throw CustomException("prediction-api-error", null)
            }
            employeePredictedMinutes["${employee.firstName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} ${employee.lastName.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }}"] = response.body!!.minutes
        }
        if (employeePredictedMinutes.keys.size < 1) {
            return mutableSetOf()
        }
        val sortedValues = employeePredictedMinutes.values.sorted()
        var index = 0
        for (value in sortedValues) {
            for (x in employeePredictedMinutes.keys) {
                if (employeePredictedMinutes[x] == value) {
                    if (index == 0) {
                        rankedEmployees.add("$x - Most Suited ($value)")
                    } else if (index == sortedValues.size - 1) {
                        rankedEmployees.add("$x - Least Suited ($value)")
                    } else {
                        rankedEmployees.add("$x - ($value)")
                    }
                    index += 1
                }
            }
        }
        return rankedEmployees
    }

    fun getSuggestedEmployeesValidation(
        projectManager: Long,
        project: Long,
        employeeType: Long
    ): String {
        if (projectRepository.isProjectManager(project = project, projectManager = projectManager) <= 0) {
            return "not-project-owner"
        }
        if (employeeTypeRepository.validEmployeeType(projectManager = projectManager, id = employeeType) <= 0) {
            return "invalid-employee-type"
        }
        return "ok"
    }

    fun getGeneratedPay(employeeId: Long, userEmail: String, project: Long): PayGenerated {
        when (getGeneratedPayValidations(
            employee = employeeId,
            projectOwner = userService.getUserId(userEmail)!!,
            project = project
        )) {
            "not-project-owner" -> throw CustomException("not-project-owner", null)
            "employee-doesn't-exist" -> throw CustomException("employee-doesn't-exist", null)
        }
        val employee = repository.findById(employeeId).get()
        val lastPaymentDateLocal : LocalDate = when(employeeWagePaymentService.noPaymentsMade(employeeId)) {
            true -> employee.joinDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            false -> employeeWagePaymentService.lastPaymentMade(employeeId).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        }
        val lastPaymentDate: Date = when(employeeWagePaymentService.noPaymentsMade(employeeId)) {
            true -> employee.joinDate
            false -> employeeWagePaymentService.lastPaymentMade(employeeId)
        }
        val currentDateLocal = Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val currentDate = Date()

        val daysBetween = when {
            lastPaymentDateLocal.isAfter(currentDateLocal) -> 0
            else -> abs(ChronoUnit.DAYS.between(lastPaymentDateLocal, currentDateLocal))
        }
        val wageToBePaid: Double = when (employee.wageType.name.lowercase()) {
            "daily" -> {
                val completeWage: Double = (daysBetween * employee.wage) / 1
                if (taskRepository.getAssignedTasksForPeriod(
                    start = lastPaymentDate,
                    end = currentDate,
                    employeeId = employeeId
                ) == 0) {
                    0.0
                } else {
                    (taskRepository.getDoneTasksForPeriod(
                        start = lastPaymentDate,
                        end = currentDate,
                        employeeId = employeeId)
                            / taskRepository.getAssignedTasksForPeriod(
                        start = lastPaymentDate,
                        end = currentDate,
                        employeeId = employeeId)) * completeWage
                }
            }
            "weekly" -> {
                val completeWage: Double = (daysBetween * employee.wage) / 7
                if (taskRepository.getAssignedTasksForPeriod(
                        start = lastPaymentDate,
                        end = currentDate,
                        employeeId = employeeId
                    ) == 0) {
                    0.0
                } else {
                    (taskRepository.getDoneTasksForPeriod(
                        start = lastPaymentDate,
                        end = currentDate,
                        employeeId = employeeId) / taskRepository.getAssignedTasksForPeriod(
                        start = lastPaymentDate,
                        end = currentDate,
                        employeeId = employeeId)) * completeWage
                }
            }
            else -> {
                val completeWage: Double = (daysBetween * employee.wage) / 30
                if (taskRepository.getAssignedTasksForPeriod(
                        start = lastPaymentDate,
                        end = currentDate,
                        employeeId = employeeId
                    ) == 0) {
                    0.0
                } else {
                    (taskRepository.getDoneTasksForPeriod(
                        start = lastPaymentDate,
                        end = currentDate,
                        employeeId = employeeId) / taskRepository.getAssignedTasksForPeriod(
                        start = lastPaymentDate,
                        end = currentDate,
                        employeeId = employeeId)) * completeWage
                }
            }
        }
        return PayGenerated(
            wageType = employee.wageType.name,
            numberOfPeriod = "$daysBetween",
            amountToPay = stringFormatter.doubleToString(wageToBePaid),
            employeeWage = stringFormatter.doubleToString(employee.wage),
            lastPaymentDate = stringFormatter.timestampToString(lastPaymentDate)
        )

    }

    fun getGeneratedPayValidations(
        employee: Long, projectOwner: Long, project: Long
    ): String {
        if (projectRepository.isProjectManager(project = project, projectManager = projectOwner) <= 0) {
            return "not-project-owner"
        }
        if (repository.employeeInProjectEmployeeId(project = project, employeeId = employee) <=0 ) {
            return "employee-doesn't-exist"
        }
        return "ok"
    }



}