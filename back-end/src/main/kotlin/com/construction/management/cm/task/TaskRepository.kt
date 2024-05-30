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
    @Query("select * from task where project = :projectId", nativeQuery = true)
    fun getProjectTasks(projectId: Long): MutableList<Task>
    @Query("select * from task where id in (select task from employee_task where employee = :employeeId)", nativeQuery = true)
    fun getEmployeeTasks(employeeId: Long): MutableList<Task>
    @Query("select count(*) from employee_task where task = :taskId and employee = :employeeId", nativeQuery = true)
    fun employeeAssignedTask(taskId: Long, employeeId: Long): Int
    @Query("select count(*) from task inner join employee_task et on et.task = task.id where lower(status) in ('todo', 'in_progress') and et.employee = :userId", nativeQuery = true)
    fun getUserIncompleteTasks(userId: Long): Int

    @Query("select round(EXTRACT(EPOCH FROM (completion_date - creation_date)) / 60, 2) as minutes from task where id = :taskId", nativeQuery = true)
    fun getTaskCompletionTime(taskId: Long): Double


}