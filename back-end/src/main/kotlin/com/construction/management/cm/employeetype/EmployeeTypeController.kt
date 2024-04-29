package com.construction.management.cm.employeetype

import com.construction.management.cm.auth.TokenService
import com.construction.management.cm.response.EmployeeTypeResponse
import com.construction.management.cm.response.WageTypeResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("api/v1/employee-type")
class EmployeeTypeController(private val service: EmployeeTypeService,
                             private val tokenService: TokenService) {
    @GetMapping("/all")
    fun getEmployeeTypes(@RequestHeader("Authorization") header:String): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val employeeTypes = service.getEmployeeTypes(userEmail = userEmail!!)
        return ResponseEntity.status(200).body(
            EmployeeTypeResponse(
                httpStatus = 200,
                message = "Employee Types Retrieved Successfully",
                employeeTypes = employeeTypes
            )
        )
    }





}