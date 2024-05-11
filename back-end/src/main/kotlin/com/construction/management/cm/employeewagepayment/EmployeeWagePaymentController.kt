package com.construction.management.cm.employeewagepayment

import com.construction.management.cm.Runner.Runner
import com.construction.management.cm.auth.TokenService
import com.construction.management.cm.response.GetPaidPeriodsResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("api/v1/wage-payment")
class EmployeeWagePaymentController(
    private val tokenService: TokenService,
    private val service: EmployeeWagePaymentService
) {

    @GetMapping("/paid")
    fun getPaidPeriods(@RequestHeader("Authorization") header:String,
                       @RequestParam(name = "employee_id") employeeId: Long): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val paidPeriods = service.getPaidPeriods(userEmail = userEmail!!, employeeId = employeeId)
        return ResponseEntity.status(200).body(
            GetPaidPeriodsResponse(
                httpStatus = 200,
                message = "Employee Paid Periods Retrieved Successfully",
                periods = paidPeriods
            )
        )
    }





}