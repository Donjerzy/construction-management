package com.construction.management.cm.employeetask

import com.construction.management.cm.dto.GetEmployeeTasks
import com.construction.management.cm.dto.MoveTask
import com.construction.management.cm.employee.EmployeeRepository
import com.construction.management.cm.exceptionhandler.CustomException
import com.construction.management.cm.project.ProjectRepository
import com.construction.management.cm.task.Task
import com.construction.management.cm.task.TaskRepository
import com.construction.management.cm.task.TaskStatus
import com.construction.management.cm.taskhistory.TaskHistory
import com.construction.management.cm.taskhistory.TaskHistoryRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class EmployeeTaskService(
    private val employeeRepository: EmployeeRepository,
    private val taskRepository: TaskRepository,
    private val projectRepository: ProjectRepository,
    private val taskHistoryRepository: TaskHistoryRepository
) {
    fun getEmployeeTasks(userId: Long, projectId: String): MutableList<GetEmployeeTasks> {
        when(getEmployeeTasksValidation(
            userId = userId,
            projectId = projectId
        )) {
            "employee-not-in-project" -> throw CustomException("employee-not-in-project", null)
            "invalid-project" -> throw CustomException("invalid-project", null)
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

    fun getEmployeeTasksValidation(userId: Long, projectId: String):String {
        if (projectRepository.projectUuidExists(UUID.fromString(projectId)) <= 0 ) {
            return "invalid-project"
        }
        if(employeeRepository.employeeInProjectEmployeeId(
            project = projectRepository.getProjectByUuid(UUID.fromString(projectId)).id,
            employeeId = userId
        ) <= 0 ) {
            return "employee-not-in-project"
        }
        return "ok"
    }

    fun moveTask(task: MoveTask, userId: Long): String {
        when(moveTaskValidations(
            userId = userId,
            task = task
        )) {
            "task-doesn't-exist" -> throw CustomException("task-doesn't-exist", null)
            "invalid-employee" -> throw CustomException("invalid-employee", null)
            "invalid-move-action" -> throw CustomException("invalid-move-action", null)
        }
        val fetchedTask = taskRepository.findById(task.taskId).get()
        fetchedTask.status = when(task.action.lowercase()) {
            "todo" -> {
                if (fetchedTask.completionDate != null) {
                    fetchedTask.completionDate = null
                }
                TaskStatus.TODO.name
            }
            "in_progress" -> {
                if (fetchedTask.completionDate != null) {
                    fetchedTask.completionDate = null
                }
                TaskStatus.IN_PROGRESS.name
            }
            else -> {
                fetchedTask.completionDate = Date()
                TaskStatus.DONE.name
            }
        }
        val savedTask = taskRepository.save(fetchedTask)
        val taskHistory = TaskHistory()
        taskHistory.taskId = savedTask.id
        taskHistory.status = savedTask.status
        taskHistory.entryDate = savedTask.creationDate
        taskHistory.user = "E$userId"
        taskHistoryRepository.save(taskHistory)
        return "Task moved successfully"
    }

    fun moveTaskValidations(
        userId: Long,
        task: MoveTask
    ): String {
        if (!taskRepository.findById(task.taskId).isPresent) {
            return "task-doesn't-exist"
        }
        if (taskRepository.employeeAssignedTask(taskId = task.taskId, employeeId = userId) <=0) {
            return "invalid-employee"
        }
        if (task.action.lowercase() !in listOf("todo", "in_progress", "done")) {
            return "invalid-move-action"
        }
        val fetchedTask = taskRepository.findById(task.taskId).get()
        val currentPosition = fetchedTask.status.lowercase()
        if (currentPosition == task.action.lowercase()) {
            return "invalid-move-action"
        }
        return "ok"
    }

}