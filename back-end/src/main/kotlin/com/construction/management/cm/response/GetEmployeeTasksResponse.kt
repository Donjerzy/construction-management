package com.construction.management.cm.response

import com.construction.management.cm.dto.GetEmployeeTasks

data class GetEmployeeTasksResponse (
    val httpStatus: Int,
    val message: String,
    val tasks: MutableList<GetEmployeeTasks>
)
