package com.construction.management.cm.dto

data class GetUnassignedTasks(
    val taskId: Long,
    val title: String,
    val status: String,
    val priority: String,
    val description: String
)
