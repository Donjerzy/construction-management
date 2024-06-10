package com.construction.management.cm.employeereport

import com.construction.management.cm.employeeauth.EmployeeAuthService
import com.construction.management.cm.employeetask.EmployeeTaskService
import com.construction.management.cm.response.EmployeeReportResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("api/v1/employee/report")
class EmployeeReport(
    private val service: EmployeeReportService,
    private val authService: EmployeeAuthService
) {

    @GetMapping("/dashboard")
    fun getDashboard(
        @RequestHeader(name = "Cmt") header: String,
    ): ResponseEntity<Any> {
        val userId = authService.validateRequestToken(token = header)
        val report = service.getDashboardReport(userId = userId)
        return ResponseEntity.status(200).body(
            EmployeeReportResponse (
                httpStatus = 200,
                message = "Dashboard report retrieved successfully",
                report = report
            )
        )
    }


}