package com.construction.management.cm.response

import com.construction.management.cm.dto.GetEmployee

data class GetEmployeeResponse (
    val httpStatus: Int,
    val message: String,
    val employee: GetEmployee
)