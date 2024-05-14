package com.construction.management.cm.dto

data class GetProjectTasks (
    val taskId: Long,
    val title: String,
    val status: String
)
