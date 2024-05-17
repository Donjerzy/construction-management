package com.construction.management.cm.response

data class GeneralReportResponse(
    val httpStatus: Int,
    val message: String,
    val report: String
)
