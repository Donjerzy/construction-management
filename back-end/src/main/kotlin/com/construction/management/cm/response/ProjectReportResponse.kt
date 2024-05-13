package com.construction.management.cm.response

import com.construction.management.cm.dto.ProjectReport

data class ProjectReportResponse(
    val httpStatus: Int,
    val message: String,
    val report: ProjectReport
)
