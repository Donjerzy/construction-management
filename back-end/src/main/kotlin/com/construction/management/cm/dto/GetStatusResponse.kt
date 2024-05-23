package com.construction.management.cm.dto

data class GetStatusResponse(
    val httpStatus: Int,
    val message: String,
    val status: String
)
