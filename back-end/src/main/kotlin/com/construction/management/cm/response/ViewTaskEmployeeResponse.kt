package com.construction.management.cm.response

import com.construction.management.cm.dto.ViewTaskEmployee

data class ViewTaskEmployeeResponse(
    val httpStatus: Int,
    val message: String,
    val task: ViewTaskEmployee
)
