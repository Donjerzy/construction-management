package com.construction.management.cm.response

import com.construction.management.cm.dto.GetEnrolledProjects
import org.springframework.http.HttpStatus

data class GetEnrolledProjectsResponse(
    val httpStatus: Int,
    val message: String,
    val projects: MutableList<GetEnrolledProjects>
)
