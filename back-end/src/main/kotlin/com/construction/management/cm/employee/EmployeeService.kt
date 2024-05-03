package com.construction.management.cm.employee

import com.construction.management.cm.Runner.Runner
import com.construction.management.cm.dto.AddEmployee
import com.construction.management.cm.employeetype.EmployeeTypeRepository
import com.construction.management.cm.exceptionhandler.CustomException
import com.construction.management.cm.formatters.StringFormatter
import com.construction.management.cm.project.ProjectRepository
import com.construction.management.cm.project.ProjectService
import com.construction.management.cm.user.UserService
import com.construction.management.cm.validator.Validator
import com.construction.management.cm.wagetype.WageTypeRepository
import org.springframework.stereotype.Service
import java.io.File

@Service
class EmployeeService(private val repository: EmployeeRepository,
                      private val userService: UserService,
                      private val validator: Validator,
                      private val employeeTypeRepository: EmployeeTypeRepository,
                      private val wageTypeRepository: WageTypeRepository,
                      private val stringFormatter: StringFormatter,
                      private val projectRepository: ProjectRepository,
                      private val runner: Runner) {

    fun getNumberOfEmployeesInProject(projectId:Long):Int {
        return repository.numberOfEmployeesInProject(projectId)
    }


    fun employeeInProject(employeeEmail: String, project: Long): Boolean {
        return repository.employeeInProject(project = project, employeeEmail = employeeEmail.lowercase()) > 0
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
        newEmployee.password = stringFormatter.toPassword(employee.password)
        newEmployee.wage = employee.wage
        newEmployee.employeeType = employeeTypeRepository.findById(employee.employeeType).get()
        newEmployee.project = projectRepository.findById(employee.project).get()
        newEmployee.wageType = wageTypeRepository.findById(employee.wageType).get()
        if (employee.contract == null) {
            repository.save(newEmployee)
        } else {
            val contractDirectoryPath = "contracts"
            val fileName = "${employee.email}-contract"
            val contractDirectory = File(contractDirectoryPath)
            if(!contractDirectory.exists()) {
                contractDirectory.mkdir()
            }
            val fileSaveLocation = File(contractDirectory.absolutePath, fileName)
            employee.contract.transferTo(fileSaveLocation)
            newEmployee.contract = fileSaveLocation.absolutePath
            repository.save(newEmployee)
        }
        return "Employee Added Successfully"
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
        if (!validator.isValidPassword(employee.password)) {
            return "invalid-password"
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


}