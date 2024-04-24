package com.construction.management.cm.response

import com.construction.management.cm.dto.ProjectClient

data class GetClientsResponse(
    val httpStatus: Int,
    val message: String,
    val clients: MutableSet<ProjectClient>
)
