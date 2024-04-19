package com.construction.management.cm.response

import com.construction.management.cm.dto.Projects

data class GetProjectsResponse(
    val httpStatus: Int,
    val message: String,
    val setContainer: MutableSet<Projects>
)

