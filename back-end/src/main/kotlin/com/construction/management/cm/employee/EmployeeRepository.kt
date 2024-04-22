package com.construction.management.cm.employee

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface EmployeeRepository: JpaRepository<Employee, Long> {

    @Query("select count(*) from employee where project = :projectId", nativeQuery = true)
    fun numberOfEmployeesInProject(projectId: Long): Int
}