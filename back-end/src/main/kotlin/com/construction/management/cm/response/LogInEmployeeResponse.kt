package com.construction.management.cm.response

import com.construction.management.cm.dto.LoggedIn
import com.construction.management.cm.dto.LoggedInEmployee

data class LogInEmployeeResponse(
    val httpStatus: Int,
    val message: String,
    val user: LoggedInEmployee
)
