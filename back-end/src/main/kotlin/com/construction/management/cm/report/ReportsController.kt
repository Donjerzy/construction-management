package com.construction.management.cm.report

import com.construction.management.cm.auth.TokenService
import com.construction.management.cm.response.GeneralReportResponse
import com.construction.management.cm.response.ProjectReportResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("api/v1/report")
class ReportsController(
    private val tokenService: TokenService,
    private val service: ReportsService
) {

    @GetMapping("/dashboard")
    fun getDashboardReport(
        @RequestHeader("Authorization") header:String,
    ): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val report = service.getDashboardReport(userEmail = userEmail!!)
        return ResponseEntity.status(200).body(
            ProjectReportResponse(
                httpStatus = 200,
                message = "Projects Report Retrieved Successfully",
                report = report
            )
        )
    }

    @GetMapping("/general")
    fun getGeneralReport(
        @RequestHeader("Authorization") header:String,
    ): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val report = service.getGeneralReport(userEmail = userEmail!!)
        return ResponseEntity.status(200).body(
            GeneralReportResponse(
                httpStatus = 200,
                message = "General report retrieved successfully",
                report = report
            )
        )
    }

    @GetMapping("/project-general")
    fun getGeneralProjectReport(@RequestHeader("Authorization") header:String,
                                @RequestParam(name = "project") project: Long): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val report = service.getProjectGeneralReport(userEmail = userEmail!!, project = project)
        return ResponseEntity.status(200).body(GeneralReportResponse (
            httpStatus = 200,
            message = "General project report retrieved successfully",
            report = report
            )
        )
    }


    @GetMapping("/project-client")
    fun getProjectClientReport(@RequestHeader("Authorization") header:String,
                                @RequestParam(name = "project") project: Long): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val report = service.getProjectClientReport(userEmail = userEmail!!, project = project)
        return ResponseEntity.status(200).body(GeneralReportResponse (
            httpStatus = 200,
            message = "Client report retrieved successfully",
            report = report
        )
        )
    }






}