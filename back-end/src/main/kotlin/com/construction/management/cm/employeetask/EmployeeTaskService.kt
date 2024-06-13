package com.construction.management.cm.employeetask

import com.construction.management.cm.dto.*
import com.construction.management.cm.emailsender.EmailSender
import com.construction.management.cm.employee.Employee
import com.construction.management.cm.employee.EmployeeRepository
import com.construction.management.cm.exceptionhandler.CustomException
import com.construction.management.cm.formatters.StringFormatter
import com.construction.management.cm.project.ProjectRepository
import com.construction.management.cm.task.Task
import com.construction.management.cm.task.TaskRepository
import com.construction.management.cm.task.TaskStatus
import com.construction.management.cm.taskcomment.TaskComment
import com.construction.management.cm.taskcomment.TaskCommentRepository
import com.construction.management.cm.taskhistory.TaskHistory
import com.construction.management.cm.taskhistory.TaskHistoryRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class EmployeeTaskService(
    private val employeeRepository: EmployeeRepository,
    private val taskRepository: TaskRepository,
    private val projectRepository: ProjectRepository,
    private val taskHistoryRepository: TaskHistoryRepository,
    private val formatter: StringFormatter,
    private val taskCommentRepository: TaskCommentRepository,
    private val emailSender: EmailSender
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
                    status = task.status,
                    priority = task.priority.lowercase()
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

    fun viewTask(taskId: Long, userId: Long): ViewTaskEmployee {
        when (viewTaskValidations(
            userId = userId,
            taskId = taskId
        )) {
            "task-doesn't-exist" -> throw CustomException("task-doesn't-exist", null)
            "invalid-employee" -> throw CustomException("invalid-employee", null)
        }
        val fetchedTask = taskRepository.findById(taskId).get()
        return ViewTaskEmployee(
           taskId = fetchedTask.id,
            completionDate = when(fetchedTask.completionDate) {
                null -> "n/a"
                else -> formatter.timestampToString(fetchedTask.completionDate!!)
            },
            creationDate = formatter.timestampToString(fetchedTask.creationDate),
            description = fetchedTask.description,
            status = fetchedTask.status,
            title = fetchedTask.name,
            taskComments = commentsToViewTask(taskCommentRepository.getTaskComments(taskId)),
            employees = employeesToViewTask(fetchedTask.employees, userId = userId),
            priority = fetchedTask.priority
        )
    }

    fun employeesToViewTask(employees: MutableSet<Employee>, userId: Long): MutableMap<Long, String> {
        val result = mutableMapOf<Long, String>()
        for (employee in employees) {
            if (employee.id == userId) {
                result[employee.id] = "You"
            } else {
                result[employee.id] = "${employee.firstName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} ${employee.lastName.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }} - ${employee.employeeType.name}"
            }
        }
        return result
    }

    fun commentsToViewTask(taskComments: MutableList<TaskComment>): MutableList<GetTaskComments> {
        val result = mutableListOf<GetTaskComments>()
        for (comment in taskComments) {
            result.add(
                GetTaskComments(
                    commenter = when(comment.commentUserId[0]) {
                        'E' -> "You"
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

    fun viewTaskValidations(
        userId: Long,
        taskId: Long
    ): String {
        if (!taskRepository.findById(taskId).isPresent) {
            return "task-doesn't-exist"
        }
        if (taskRepository.employeeAssignedTask(taskId = taskId, employeeId = userId) <=0) {
            return "invalid-employee"
        }
        return "ok"
    }

    fun addComment(userId: Long, addComment: AddComment): String {
        when (addCommentValidations(
            userId = userId,
            addComment = addComment
        )) {
            "task-doesn't-exist" -> throw CustomException("task-doesn't-exist", null)
            "invalid-employee" -> throw CustomException("invalid-employee", null)
            "empty-comment" -> throw CustomException("empty-comment", null)
        }
        val user = employeeRepository.findById(userId).get()
        val comment = TaskComment()
        comment.comment = addComment.comment
        comment.commentUserId = "E$userId"
        comment.authorFirstName = user.firstName.lowercase()
        comment.authorSurname = user.lastName.lowercase()
        comment.task = taskRepository.findById(addComment.taskId).get()
        taskCommentRepository.save(comment)

        val currentDateTime = formatter.localDateTimeToString(LocalDateTime.now())

        // comment notifications.
        val assignedEmployees = employeeRepository.getEmployeesInTask(addComment.taskId)
        if (assignedEmployees.size > 0) {
            val toAlert = mutableMapOf<String, String>()
            for (employee in assignedEmployees) {
                if (employee.email.lowercase() != user.email.lowercase()) {
                    toAlert[employee.email.lowercase()] = "${employee.firstName.lowercase().capitalize()} ${employee.lastName.lowercase().capitalize()}"
                }

            }
            // add project manager to mailing list
            val projectManager = taskRepository.findById(addComment.taskId).get().project.projectManager
            toAlert[projectManager.email.lowercase()] = "${projectManager.firstName.lowercase().capitalize()} ${projectManager.surname.lowercase().capitalize()}"
            val payload = CommentAddedNotification (
                comment = addComment.comment,
                toAlert = toAlert,
                commenter = "${user.firstName.lowercase().capitalize()} ${user.lastName.lowercase().capitalize()}",
                task = taskRepository.findById(addComment.taskId).get().name,
                date = currentDateTime,
                project = taskRepository.findById(addComment.taskId).get().project.name
            )
            emailSender.commentAddedNotification(payload = payload)
        }

        return "Comment added successfully"
    }

    fun addCommentValidations(userId: Long, addComment: AddComment): String {
        if (!taskRepository.findById(addComment.taskId).isPresent) {
            return "task-doesn't-exist"
        }
        if (taskRepository.employeeAssignedTask(taskId = addComment.taskId, employeeId = userId) <=0) {
            return "invalid-employee"
        }
        if (addComment.comment.isBlank() || addComment.comment.isEmpty()) {
            return "empty-comment"
        }
        return "ok"
    }

    fun getIncompleteTasks(userId: Long): Int {
        return taskRepository.getUserIncompleteTasks(userId = userId)
    }

}