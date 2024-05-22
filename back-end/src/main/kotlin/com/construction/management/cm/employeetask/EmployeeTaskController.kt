package com.construction.management.cm.employeetask

import com.construction.management.cm.dto.AddComment
import com.construction.management.cm.dto.GetEmployeeTaskReq
import com.construction.management.cm.dto.MoveTask
import com.construction.management.cm.employeeauth.EmployeeAuthService
import com.construction.management.cm.response.DefaultNa
import com.construction.management.cm.response.GetEmployeeTasksResponse
import com.construction.management.cm.response.GetIncompleteTasksResponse
import com.construction.management.cm.response.ViewTaskEmployeeResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("api/v1/employee/task")
class EmployeeTaskController (
    private val service: EmployeeTaskService,
    private val authService: EmployeeAuthService
) {

    @GetMapping("/all")
    fun getAllEmployeeTasks(
        @RequestHeader(name = "Cmt") header: String,
        @RequestParam(name = "projectId") projectId: String,
        //@RequestBody token: GetEmployeeTaskReq
    ): ResponseEntity<Any> {
        val userId = authService.validateRequestToken(token = header)
        val tasks = service.getEmployeeTasks(userId = userId, projectId = projectId)
        return ResponseEntity.status(200).body(GetEmployeeTasksResponse(
            httpStatus = 200,
            message = "Tasks retrieved successfully",
            tasks = tasks
        ))

    }

    @PostMapping("/move")
    fun moveTask(
        @RequestHeader(name = "Cmt") header: String,
        @RequestBody task: MoveTask
    ): ResponseEntity<Any> {
        val userId = authService.validateRequestToken(token = header)
        val message = service.moveTask(task = task, userId = userId)
        return ResponseEntity.status(200).body(DefaultNa(
            httpStatus = 200,
            message = message
        ))
    }

    @GetMapping("/view")
    fun viewTask(
        @RequestHeader(name = "Cmt") header: String,
        @RequestParam(name="taskId") taskId: Long
    ): ResponseEntity<Any> {
        val userId = authService.validateRequestToken(token = header)
        val task = service.viewTask(taskId = taskId, userId = userId)
        return ResponseEntity.status(200).body(
            ViewTaskEmployeeResponse (
                httpStatus = 200,
                message = "Employee task retrieved successfully",
                task = task
            )
        )
    }

    @PostMapping("/add-comment")
    fun addComment(
        @RequestHeader(name = "Cmt") header: String,
        @RequestBody addComment: AddComment
    ): ResponseEntity<Any> {
        val userId = authService.validateRequestToken(token = header)
        val message = service.addComment(userId = userId, addComment = addComment)
        return ResponseEntity.status(200).body(
            DefaultNa (
                httpStatus = 200,
                message = message
            )
        )
    }

    @GetMapping("/incomplete")
    fun getIncompleteTasks(
        @RequestHeader(name = "Cmt") header: String
    ): ResponseEntity<Any> {
        val userId = authService.validateRequestToken(token = header)
        val count = service.getIncompleteTasks(userId = userId)
        return ResponseEntity.status(200).body(
            GetIncompleteTasksResponse(
                httpStatus = 200,
                message = "Count retrieved successfully",
                count = count
            )
        )
    }




}