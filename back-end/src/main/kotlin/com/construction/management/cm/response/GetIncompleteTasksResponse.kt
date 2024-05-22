package com.construction.management.cm.response

data class GetIncompleteTasksResponse(
    val httpStatus: Int,
    val message: String,
    val count: Int
)
