package com.construction.management.cm.employeeauth

import com.construction.management.cm.dto.EmployeeLogIn
import com.construction.management.cm.dto.EmployeeLogOut
import com.construction.management.cm.dto.TestAuth
import com.construction.management.cm.response.DefaultNa
import com.construction.management.cm.response.LogInEmployeeResponse
import com.construction.management.cm.response.LoginResponse
import org.springframework.http.RequestEntity
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
    fun logIn (@RequestBody employeeLogIn: EmployeeLogIn): ResponseEntity<Any> {
        val user = service.logIn(employeeLogIn)
        return ResponseEntity.status(200).body(
            LogInEmployeeResponse(
                httpStatus = 200,
                message = "Employee authenticated successfully",
                user = user
            )
        )
    }

    @PostMapping("/logout")
    fun logout (@RequestHeader(name = "Cmt") token: String): ResponseEntity<Any> {
        val user = service.validateRequestToken(token)
        val message = service.logOut(user = user)
        return ResponseEntity.status(200).body(
            DefaultNa (
                httpStatus = 200,
                message = message
            )
        )
    }


}