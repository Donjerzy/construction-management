package com.construction.management.cm.response

data class GetProjectCodeResponse(
    val httpStatus: Int,
    val message: String,
    val code: String
)
