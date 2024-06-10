package com.construction.management.cm.dto

data class EmployeeReportDto (
    val toDo: Int,
    val inProgress: Int,
    val done: Int,
    val averageTaskCompletionTime: String
)
