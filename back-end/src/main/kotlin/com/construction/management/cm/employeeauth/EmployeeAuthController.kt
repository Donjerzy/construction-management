package com.construction.management.cm.employeeauth

import com.construction.management.cm.dto.EmployeeLogIn
import com.construction.management.cm.dto.TestAuth
import com.construction.management.cm.response.LoginResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("api/v1/employee/auth")
class EmployeeAuthController(
    private val service: EmployeeAuthService
) {

    @PostMapping("/login")
    fun logIn(@RequestBody employeeLogIn: EmployeeLogIn): ResponseEntity<Any> {
        val user = service.logIn(employeeLogIn)
        return ResponseEntity.status(200).body(
            LoginResponse(
                httpStatus = 200,
                message = "Employee authenticated successfully",
                user = user
            )
        )
    }


}