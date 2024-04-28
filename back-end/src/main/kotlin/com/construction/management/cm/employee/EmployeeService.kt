package com.construction.management.cm.employee

import com.construction.management.cm.dto.AddEmployee
import org.springframework.stereotype.Service

@Service
class EmployeeService(private val repository: EmployeeRepository) {

    fun getNumberOfEmployeesInProject(projectId:Long):Int {
        return repository.numberOfEmployeesInProject(projectId)
    }

    fun addEmployee(employee: AddEmployee, userEmail: String?): String {
        return "Employee Added Successfully"
    }


}