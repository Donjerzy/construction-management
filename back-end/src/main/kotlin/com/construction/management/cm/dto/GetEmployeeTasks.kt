package com.construction.management.cm.dto

data class GetEmployeeTasks(
    val taskId: Long,
    val title: String,
    val status: String
)

