package com.construction.management.cm.employeetask

import com.construction.management.cm.dto.GetEmployeeTaskReq
import com.construction.management.cm.employeeauth.EmployeeAuthService
import com.construction.management.cm.response.GetEmployeeTasksResponse
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



}