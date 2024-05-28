package com.construction.management.cm.task

import com.construction.management.cm.ai.TaskAssignmentRepository
import com.construction.management.cm.dto.*
import com.construction.management.cm.employee.Employee
import com.construction.management.cm.employee.EmployeeRepository
import com.construction.management.cm.exceptionhandler.CustomException
import com.construction.management.cm.formatters.StringFormatter
import com.construction.management.cm.project.ProjectRepository
import com.construction.management.cm.taskcomment.TaskComment
import com.construction.management.cm.taskcomment.TaskCommentRepository
import com.construction.management.cm.taskhistory.TaskHistory
import com.construction.management.cm.taskhistory.TaskHistoryRepository
import com.construction.management.cm.user.User
import com.construction.management.cm.user.UserService
import jakarta.persistence.Column
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import org.springframework.stereotype.Service
import java.util.*

@Service
class TaskService(private val repository: TaskRepository,
                private val userService: UserService,
                private val projectRepository: ProjectRepository,
                private val employeeRepository: EmployeeRepository,
                private val taskHistoryRepository: TaskHistoryRepository,
                private val formatter: StringFormatter,
                private val taskCommentRepository: TaskCommentRepository,
                private val taskAssignmentRepository: TaskAssignmentRepository) {

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
        taskHistory.user = "O${userService.getUserId(userEmail)!!}"
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
        val savedTask = repository.save(fetchedTask)
        val taskHistory = TaskHistory()
        taskHistory.taskId = savedTask.id
        taskHistory.status = savedTask.status
        taskHistory.entryDate = savedTask.creationDate
        taskHistory.user = "O${userService.getUserId(userEmail)!!}"
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

    fun getTask(userEmail: String, taskId: Long): ViewTask {
        when(getTaskValidations(
            projectOwner = userService.getUserId(userEmail)!!,
            taskId = taskId
        )) {
            "task-doesn't-exist" -> throw CustomException("task-doesn't-exist", null)
            "not-project-owner" -> throw CustomException("not-project-owner", null)
        }
        val fetchedTask = repository.findById(taskId).get()
        return ViewTask (
            taskId = taskId,
            title = fetchedTask.name,
            creationDate = formatter.timestampToString(fetchedTask.creationDate),
            completionDate = when(fetchedTask.completionDate) {
                null -> "n/a"
                else -> formatter.timestampToString(fetchedTask.completionDate!!)
            },
            description = fetchedTask.description,
            status = fetchedTask.status,
            employees = employeesToViewTask(fetchedTask.employees),
            taskHistory = taskHistoryToViewTask(taskHistoryRepository.getTaskHistory(taskId)),
            taskComments = commentsToViewTask(taskCommentRepository.getTaskComments(taskId))
        )
    }

    fun commentsToViewTask(taskComments: MutableList<TaskComment>): MutableList<GetTaskComments> {
        val result = mutableListOf<GetTaskComments>()
        for (comment in taskComments) {
            result.add(
                GetTaskComments(
                    commenter = when(comment.commentUserId[0]) {
                        'O' -> "You"
                        else -> "${comment.authorFirstName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} ${comment.authorSurname.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.getDefault()
                            ) else it.toString()
                        }}"
                    },
                    date = formatter.timestampToString(comment.date),
                    comment = comment.comment
                )
            )
        }
        return result
    }

    fun taskHistoryToViewTask(taskHistory: MutableList<TaskHistory>): MutableList<GetTaskHistory> {
        val result = mutableListOf<GetTaskHistory>()
        for (task in taskHistory) {
            result.add(
                GetTaskHistory (
                    date = formatter.timestampToString(task.entryDate),
                    status = task.status,
                    user = when(task.user[0]) {
                        'O' -> "You"
                        else -> {
                            val employeeId: String = task.user.slice(1 ..< task.user.length)
                            val employee = employeeRepository.findById(employeeId.toLong()).get()
                            "${employee.firstName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} ${employee.lastName.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.getDefault()
                                ) else it.toString()
                            }}"
                        }
                    }
                )
            )
        }
        return result
    }

    fun employeesToViewTask(employees: MutableSet<Employee>): MutableMap<Long, String> {
        val result = mutableMapOf<Long, String>()
        for (employee in employees) {
            result[employee.id] = "${employee.firstName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} ${employee.lastName.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }} - ${employee.employeeType.name}"
        }
        return result
    }

    fun getTaskValidations(
        projectOwner: Long,
        taskId: Long
    ): String {
        if (!repository.findById(taskId).isPresent) {
            return "task-doesn't-exist"
        }
        val task = repository.findById(taskId).get()
        if (task.project.projectManager.id != projectOwner) {
            return "not-project-owner"
        }
        return "ok"
    }

    fun addComment(userEmail: String, addComment: AddComment): String {
        when (addCommentValidations(
            projectOwner = userService.getUserId(userEmail)!!,
            addComment = addComment
        )) {
            "task-doesn't-exist" -> throw CustomException("task-doesn't-exist", null)
            "empty-comment" -> throw CustomException("empty-comment", null)
            "not-project-owner" -> throw CustomException("not-project-owner", null)
        }
        val user: User = userService.getUser(userEmail)!!
        val comment = TaskComment()
        comment.comment = addComment.comment
        comment.commentUserId = "O${user.id}"
        comment.authorFirstName = user.firstName.lowercase()
        comment.authorSurname = user.surname.lowercase()
        comment.task = repository.findById(addComment.taskId).get()
        taskCommentRepository.save(comment)
        return "Comment added successfully"
    }

    fun addCommentValidations(
        projectOwner: Long,
        addComment: AddComment
    ): String {
        if (!repository.findById(addComment.taskId).isPresent) {
            return "task-doesn't-exist"
        }
        if (addComment.comment.isBlank() || addComment.comment.isEmpty()) {
            return "empty-comment"
        }
        val task = repository.findById(addComment.taskId).get()
        if (task.project.projectManager.id != projectOwner) {
            return "not-project-owner"
        }
        return "ok"
    }


}