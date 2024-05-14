package com.construction.management.cm.response

import com.construction.management.cm.dto.GetProjectTasks

data class GetProjectTasksResponse (
    val httpStatus: Int,
    val message: String,
    val tasks: MutableSet<GetProjectTasks>
)
