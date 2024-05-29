package com.construction.management.cm.employee

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface EmployeeRepository: JpaRepository<Employee, Long> {

    @Query("select count(*) from employee where project = :projectId", nativeQuery = true)
    fun numberOfEmployeesInProject(projectId: Long): Int

    @Query("select count(*) from employee where lower(email) = :employeeEmail and project = :project", nativeQuery = true)
    fun employeeInProject(project: Long, employeeEmail: String): Int

    @Query("select count(*) from employee where project = :project and id = :employeeId", nativeQuery = true)
    fun employeeInProjectEmployeeId(project: Long, employeeId: Long): Int
    @Query("select count(*) from employee inner join project on employee.project = project.id where lower(employee.email) = :email and project_id = :project", nativeQuery = true)
    fun employeeInProjectEmployeeIdProjectUid(project:UUID, email: String): Int

    @Query("select employee.* from employee inner join project on employee.project = project.id where lower(employee.email) = :email and project_id = :project", nativeQuery = true)
    fun getEmployeeEmailProjectUid(project:UUID, email: String): Employee

    @Query("select * from employee where project = :project", nativeQuery = true)
    fun getProjectEmployees(project: Long): MutableList<Employee>
    @Query("select * from employee where project = :project and employee_type = :type", nativeQuery = true)
    fun getProjectEmployeesByType(project: Long, type: Long): MutableList<Employee>
    @Query("select count(*) from task t inner join employee_task et on et.task = t.id where lower(status) in ('todo', 'in_progress') and et.employee = :employeeId", nativeQuery = true)
    fun getEmployeeActiveTasks(employeeId: Long): Int
    @Query("select count(*) from task t inner join employee_task et on et.task = t.id where lower(status) = 'done' and et.employee = :employeeId", nativeQuery = true)
    fun getEmployeeDoneTasks(employeeId: Long): Int
    @Query("select avg(round(EXTRACT(EPOCH FROM (t.completion_date - t.creation_date)) / 60,2)) as minutes from task t inner join employee_task et on et.task = t.id where lower(t.status) = 'done' and et.employee = :employeeId group by et.employee", nativeQuery = true)
    fun getAverageTasksCompletionTime(employeeId: Long): Double


}