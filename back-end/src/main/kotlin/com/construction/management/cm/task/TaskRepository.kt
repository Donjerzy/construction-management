package com.construction.management.cm.task

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository: JpaRepository<Task, Long> {

    @Query("select count(*) from task where project = :project and lower(status) = 'ongoing'", nativeQuery = true)
    fun getProjectPendingTasksCount(project:Long): Int

    @Query("select count(*) from task where project = :project and lower(status) like '%done'", nativeQuery = true)
    fun getProjectDoneTasksCount(project:Long): Int

    @Query("select count(*) from employee_task inner join task on task.id = employee_task.task and lower(task.status) like '%done%' where employee = :employeeId", nativeQuery = true)
    fun getEmployeeDoneTasks(employeeId: Long): Int

    @Query("select count(*) from employee_task inner join task on task.id = employee_task.task and lower(task.status) like '%ongoing%' where employee = :employeeId", nativeQuery = true)
    fun getEmployeeOngoingTasks(employeeId: Long): Int

    @Query("select count(*) from employee_task inner join task on task.id = employee_task.task and lower(task.status) = 'done_on_time' where employee = :employeeId", nativeQuery = true)
    fun getEmployeeDoneOnTimeTasks(employeeId: Long): Int

    @Query("select count(*) from employee_task inner join task on task.id = employee_task.task and lower(task.status) = 'done_late' where employee = :employeeId", nativeQuery = true)
    fun getEmployeeDonePastTimeTasks(employeeId: Long): Int





}