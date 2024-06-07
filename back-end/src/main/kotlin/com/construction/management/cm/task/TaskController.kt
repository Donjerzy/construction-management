package com.construction.management.cm.task

import com.construction.management.cm.auth.TokenService
import com.construction.management.cm.dto.AddComment
import com.construction.management.cm.dto.AddTask
import com.construction.management.cm.dto.AssignEmployees
import com.construction.management.cm.dto.MoveTask
import com.construction.management.cm.response.DefaultNa
import com.construction.management.cm.response.GetProjectTasksResponse
import com.construction.management.cm.response.GetUnassignedTasksResponse
import com.construction.management.cm.response.ViewTaskResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("api/v1/task")
class TaskController(
    private val tokenService: TokenService,
    private val service: TaskService
) {

    @PostMapping("/add-task")
    fun addTask(@RequestHeader("Authorization") header:String,
                @RequestBody addTask: AddTask): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val message = service.addTask(userEmail = userEmail!!, addTask = addTask)
        return ResponseEntity.status(200).body(
            DefaultNa (
                httpStatus = 200,
                message = message
            )
        )
    }
    @GetMapping("/project/assigned")
    fun getProjectTaskList (
        @RequestHeader("Authorization") header:String,
        @RequestParam("project") project: Long
    ): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val tasks = service.getProjectsTasks(project = project, userEmail = userEmail!!)
        return ResponseEntity.status(200).body(
            GetProjectTasksResponse (
                httpStatus = 200,
                message = "Project tasks retrieved successfully",
                tasks = tasks
            )
        )
    }

    @GetMapping("/project/unassigned")
    fun getUnassignedTasks (
        @RequestHeader("Authorization") header:String,
        @RequestParam("project") project: Long
    ): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val tasks = service.getProjectUnassignedTasks(project = project, userEmail = userEmail!!)
        return ResponseEntity.status(200).body(
            GetUnassignedTasksResponse (
                httpStatus = 200,
                message = "Project tasks retrieved successfully",
                tasks = tasks
            )
        )
    }


//    @GetMapping("/unassigned/details")
//    fun getUnassignedDetails(): ResponseEntity<Any> {
//
//    }


    @PostMapping("/assign")
    fun assignEmployee (
        @RequestHeader("Authorization") header:String,
        @RequestBody employees: AssignEmployees
    ): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val message = service.assignEmployees(userEmail = userEmail!!, employees = employees)
        return ResponseEntity.status(200).body(
            DefaultNa (
                httpStatus = 200,
                message = message
            )
        )

    }

    @PostMapping("/move")
    fun moveTask(@RequestHeader("Authorization") header:String,
                 @RequestBody task: MoveTask
    ): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val message = service.moveTask(userEmail = userEmail!!, task = task)
        return ResponseEntity.status(200).body(
            DefaultNa(
                httpStatus = 200,
                message = message
            )
        )

    }

    @GetMapping("/view")
    fun viewTask(@RequestHeader("Authorization") header:String,
                 @RequestParam taskId: Long ): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val task = service.getTask(userEmail = userEmail!!, taskId = taskId)
        return ResponseEntity.status(200).body(
            ViewTaskResponse (
                httpStatus = 200,
                message = "Task retrieved successfully",
                task = task
            )
        )
    }

    @PostMapping("/add-comment")
    fun addComment(@RequestHeader("Authorization") header:String,
                   @RequestBody addComment: AddComment) : ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val message = service.addComment(userEmail = userEmail!!, addComment = addComment)
        return ResponseEntity.status(200).body(DefaultNa (
            httpStatus = 200,
            message = message
        ))
    }


}