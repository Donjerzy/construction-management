package com.construction.management.cm.response

import com.construction.management.cm.dto.GetUnassignedTasks

data class GetUnassignedTasksResponse(
    val httpStatus: Int,
    val message: String,
    val tasks: MutableSet<GetUnassignedTasks>
)


