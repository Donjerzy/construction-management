package com.construction.management.cm.response

data class GetSuggestedEmployeesResponse(
    val httpStatus: Int,
    val message: String,
    val employees: MutableSet<String>
)
