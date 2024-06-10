package com.construction.management.cm.response

import com.construction.management.cm.dto.EmployeeReportDto

data class EmployeeReportResponse (
    val httpStatus: Int,
    val message: String,
    val report: EmployeeReportDto
)
