package com.construction.management.cm.dto

data class GetEmployee(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val employeeType: String,
    val email: String,
    val tasksCompleted: Int,
    val tasksCompletedOnTime: Int,
    val tasksCompletedPastTime: Int,
    val tasksOngoing: Int,
    val wage: String,
    val wageType: String,
    val archetype: String,
    val totalTasks: Int,
    val joinDate: String,
    val hasContract: String,
    val wagesPaid: String
)
