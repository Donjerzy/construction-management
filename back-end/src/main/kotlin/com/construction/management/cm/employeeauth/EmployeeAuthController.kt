package com.construction.management.cm.employeeauth

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("api/v1/employee/auth")
class EmployeeAuthController {

    @GetMapping("/")
    fun test(): ResponseEntity<Any> {
        return ResponseEntity.status(200).body("Hello from employee auth")
    }

}