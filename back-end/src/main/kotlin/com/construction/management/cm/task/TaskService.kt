package com.construction.management.cm.task

import com.construction.management.cm.dto.AddTask
import com.construction.management.cm.dto.EmployeeTaskBreakdown
import com.construction.management.cm.dto.GetProjectTasks
import com.construction.management.cm.dto.ProjectTaskStatus
import com.construction.management.cm.employee.Employee
import com.construction.management.cm.employee.EmployeeRepository
import com.construction.management.cm.exceptionhandler.CustomException
import com.construction.management.cm.project.ProjectRepository
import com.construction.management.cm.user.UserService
import org.springframework.stereotype.Service
import java.util.*

@Service
class TaskService(private val repository: TaskRepository,
                private val userService: UserService,
                private val projectRepository: ProjectRepository,
                private val employeeRepository: EmployeeRepository) {

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
        repository.save(task)
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

    fun getProjectsTasks(project: Long, userEmail: String): MutableSet<GetProjectTasks> {
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


}