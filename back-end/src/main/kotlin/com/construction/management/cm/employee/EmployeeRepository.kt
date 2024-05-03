package com.construction.management.cm.employee

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface EmployeeRepository: JpaRepository<Employee, Long> {

    @Query("select count(*) from employee where project = :projectId", nativeQuery = true)
    fun numberOfEmployeesInProject(projectId: Long): Int

    @Query("select count(*) from employee where lower(email) = :employeeEmail and project = :project", nativeQuery = true)
    fun employeeInProject(project: Long, employeeEmail: String): Int

    @Query("select count(*) from employee where project = :project and id = :employeeId", nativeQuery = true)
    fun employeeInProjectEmployeeId(project: Long, employeeId: Long): Int
    @Query("select * from employee where project = :project", nativeQuery = true)
    fun getProjectEmployees(project: Long): MutableList<Employee>
}