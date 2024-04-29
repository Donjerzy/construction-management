package com.construction.management.cm.employee

import com.construction.management.cm.auth.TokenService
import com.construction.management.cm.dto.AddEmployee
import com.construction.management.cm.response.DefaultNa
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping


@RequestMapping("api/v1/employee")
@Controller
class EmployeeController(private val service: EmployeeService,
                        private val tokenService: TokenService) {

    @PostMapping("/add")
    fun addEmployee(@RequestHeader("Authorization") header:String,
                    @RequestBody employee: AddEmployee): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val message: String = service.addEmployee(employee = employee, userEmail = userEmail!!)
        return ResponseEntity.status(200).body(
            DefaultNa(
                httpStatus = 200,
                message = message
            )
        )
    }


}