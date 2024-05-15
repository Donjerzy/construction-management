package com.construction.management.cm.task

import com.construction.management.cm.dto.*
import com.construction.management.cm.employee.Employee
import com.construction.management.cm.employee.EmployeeRepository
import com.construction.management.cm.exceptionhandler.CustomException
import com.construction.management.cm.project.ProjectRepository
import com.construction.management.cm.taskhistory.TaskHistory
import com.construction.management.cm.taskhistory.TaskHistoryRepository
import com.construction.management.cm.user.UserService
import jakarta.persistence.Column
import org.springframework.stereotype.Service
import java.util.*

@Service
class TaskService(private val repository: TaskRepository,
                private val userService: UserService,
                private val projectRepository: ProjectRepository,
                private val employeeRepository: EmployeeRepository,
                private val taskHistoryRepository: TaskHistoryRepository) {

    fun getProjectTaskStatus(project: Long): ProjectTaskStatus {
        return ProjectTaskStatus(
            done = repository.getProjectDoneTasksCount(project),
            ongoing = repository.getProjectPendingTasksCount(project)
        )
    }

    fun getEmployeeTasksBreakDown(employee: Long): EmployeeTaskBreakdown {
        return EmployeeTaskBreakdown(
            done = repository.getEmployeeDoneTasks(employee),
            doneOnTime = repository.getEmployeeDoneOnTimeTasks(employee),
            donePastTime = repository.getEmployeeDonePastTimeTasks(employee),
            ongoing = repository.getEmployeeOngoingTasks(employee)
        )
    }

    fun addTask(userEmail: String, addTask: AddTask): String {
        when(addTaskValidations(
            addTask = addTask,
            projectOwner = userService.getUserId(userEmail)!!
        )) {
            "invalid-project" -> throw CustomException("invalid-project", null)
            "not-project-owner" -> throw CustomException("not-project-owner", null)
            "invalid-employee" -> throw CustomException("invalid-employee", null)
            "invalid-title" -> throw CustomException("invalid-title", null)
        }
        val task = Task()
        task.name = addTask.title.trim().lowercase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        task.description = addTask.description.trim()
        task.status = TaskStatus.TODO.name
        task.creationDate = Date()
        task.completionDate = null
        task.project = projectRepository.findById(addTask.project).get()
        val employees = mutableSetOf<Employee>()
        for (employee in addTask.employees) {
            employees.add(
                employeeRepository.findById(employee).get()
            )
        }
        task.employees = employees
        val savedTask = repository.save(task)
        val taskHistory = TaskHistory()
        taskHistory.taskId = savedTask.id
        taskHistory.status = savedTask.status
        taskHistory.entryDate = savedTask.creationDate
        taskHistory.user = userService.getUserId(userEmail)!!
        taskHistoryRepository.save(taskHistory)
        return "Task added successfully"
    }

    fun addTaskValidations (
        addTask: AddTask,
        projectOwner: Long
    ): String {
        /**
         * Does project exist?
         * Is the individual the owner?
         * Is each employee a member of the project?
         */
        if(!projectRepository.findById(addTask.project).isPresent) {
            return "invalid-project"
        }
        val project = projectRepository.findById(addTask.project).get()
        if (project.projectManager.id != projectOwner) {
            return "not-project-owner"
        }
        if (addTask.title.isBlank() || addTask.title.isEmpty()) {
            return "invalid-title"
        }
        for (employee in addTask.employees) {
            if (!employeeRepository.findById(employee).isPresent) {
                return "invalid-employee"
            }
            if (employeeRepository.findById(employee).get().project.id != project.id) {
                return "invalid-employee"
            }
        }
        return "ok"
    }

    fun getProjectsTasks (project: Long, userEmail: String): MutableSet<GetProjectTasks> {
       when (getProjectTasksValidations(
           project = project,
           projectOwner = userService.getUserId(userEmail)!!
       )) {
           "invalid-project" -> throw CustomException("invalid-project", null)
           "not-project-owner" -> throw CustomException("not-project-owner", null)
       }
        val tasks = repository.getProjectTasks(projectId = project)
        val result = mutableSetOf<GetProjectTasks>()
        for (task in tasks) {
            result.add (
                GetProjectTasks (
                    taskId = task.id,
                    title = task.name,
                    status = task.status.lowercase()
                )
            )
        }
        return result
    }

    fun getProjectTasksValidations (
        projectOwner: Long,
        project: Long
    ): String {
        if(!projectRepository.findById(project).isPresent) {
            return "invalid-project"
        }
        val fetchedProject = projectRepository.findById(project).get()
        if (fetchedProject.projectManager.id != projectOwner) {
            return "not-project-owner"
        }
        return "ok"
    }

    fun moveTask(userEmail: String, task: MoveTask): String {
        when (moveTaskValidations(
            projectOwner = userService.getUserId(userEmail)!!,
            task = task
        )) {
            "task-doesn't-exist" -> throw CustomException("task-doesn't-exist", null)
            "not-project-owner" -> throw CustomException("not-project-owner", null)
            "invalid-move-action" -> throw CustomException("invalid-move-action", null)
        }
        val fetchedTask = repository.findById(task.taskId).get()
        fetchedTask.status = when(task.action.lowercase()) {
            "todo" -> TaskStatus.TODO.name
            "in_progress" -> TaskStatus.IN_PROGRESS.name
            else -> TaskStatus.DONE.name
        }
        val savedTask = repository.save(fetchedTask)
        val taskHistory = TaskHistory()
        taskHistory.taskId = savedTask.id
        taskHistory.status = savedTask.status
        taskHistory.entryDate = savedTask.creationDate
        taskHistory.user = userService.getUserId(userEmail)!!
        taskHistoryRepository.save(taskHistory)
        return "Task moved successfully"
    }

    fun moveTaskValidations(task: MoveTask, projectOwner: Long): String {
        if (!repository.findById(task.taskId).isPresent) {
            return "task-doesn't-exist"
        }
        val fetchedProjectOwner = repository.findById(task.taskId).get().project.projectManager.id
        if (fetchedProjectOwner != projectOwner) {
            return "not-project-owner"
        }
        if (task.action.lowercase() !in listOf("todo", "in_progress", "done")) {
            return "invalid-move-action"
        }
        val fetchedTask = repository.findById(task.taskId).get()
        val currentPosition = fetchedTask.status.lowercase()
        if (currentPosition == task.action.lowercase()) {
            return "invalid-move-action"
        }
        return "ok"
    }



}