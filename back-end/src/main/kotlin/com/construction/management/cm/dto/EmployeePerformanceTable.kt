package com.construction.management.cm.dto

data class EmployeePerformanceTable (
    val rank: String,
    val employeeType: String,
    val totalTasks:String,
    val closedTasks: String,
    val openTasks: String,
    val averageCompletionTime: String,
    val employeeName: String
)
