package com.construction.management.cm.employee

import org.springframework.stereotype.Service

@Service
class EmployeeService(private val repository: EmployeeRepository) {

    fun getNumberOfEmployeesInProject(projectId:Long):Int {
        return repository.numberOfEmployeesInProject(projectId)
    }


}