package com.construction.management.cm.employeetype

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface EmployeeTypeRepository: JpaRepository<EmployeeType, Long> {
    @Query("select count(*) from employee_type", nativeQuery = true)
    fun isEmpty(): Int

    @Query("select count(*) from employee_type where id = :id", nativeQuery = true)
    fun exists(id: Long): Int
    @Query("select * from employee_type where project_manager = :user", nativeQuery = true)
    fun getUserEmployeeTypes(user: Long): MutableList<EmployeeType>

    @Query("select count(*) from employee_type where project_manager = :projectManager and id = :id", nativeQuery = true)
    fun validEmployeeType(projectManager: Long, id: Long): Int

}