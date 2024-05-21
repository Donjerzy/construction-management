package com.construction.management.cm.employeetask

import com.construction.management.cm.dto.GetEmployeeTasks
import com.construction.management.cm.employee.EmployeeRepository
import com.construction.management.cm.exceptionhandler.CustomException
import com.construction.management.cm.task.Task
import com.construction.management.cm.task.TaskRepository
import org.springframework.stereotype.Service

@Service
class EmployeeTaskService(
    private val employeeRepository: EmployeeRepository,
    private val taskRepository: TaskRepository
) {
    fun getEmployeeTasks(userId: Long, projectId: Long): MutableList<GetEmployeeTasks> {
        when(getEmployeeTasksValidation(
            userId = userId,
            projectId = projectId
        )) {
            "employee-not-in-project" -> throw CustomException("employee-not-in-project", null)
        }
        val employeeTasks = taskRepository.getEmployeeTasks(userId)
        return tasksToGetEmployeeTasks(employeeTasks)
    }

    fun tasksToGetEmployeeTasks(tasks: MutableList<Task>): MutableList<GetEmployeeTasks> {
        val result = mutableListOf<GetEmployeeTasks>()
        for (task in tasks) {
            result.add(
                GetEmployeeTasks(
                    taskId = task.id,
                    title = task.name,
                    status = task.status
                )
            )
        }
        return result
    }

    fun getEmployeeTasksValidation(userId: Long, projectId: Long):String {
        if(employeeRepository.employeeInProjectEmployeeId(
            project = projectId,
            employeeId = userId
        ) <= 0 ) {
            return "employee-not-in-project"
        }
        return "ok"
    }
}