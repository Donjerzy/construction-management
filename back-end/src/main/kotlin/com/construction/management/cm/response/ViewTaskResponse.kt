package com.construction.management.cm.response

import com.construction.management.cm.dto.ViewTask

data class ViewTaskResponse (
    val httpStatus: Int,
    val message: String,
    val task: ViewTask
)
