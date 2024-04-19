package com.construction.management.cm.response

import com.construction.management.cm.dto.LoggedIn

data class LoginResponse(
    val httpStatus: Int,
    val message: String,
    val user: LoggedIn
)
