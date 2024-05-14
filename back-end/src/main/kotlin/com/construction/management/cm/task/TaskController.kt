package com.construction.management.cm.task

import com.construction.management.cm.auth.TokenService
import com.construction.management.cm.dto.AddTask
import com.construction.management.cm.response.DefaultNa
import com.construction.management.cm.user.UserService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping

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


}