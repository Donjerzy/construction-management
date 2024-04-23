package com.construction.management.cm.dto

data class Overview(
    val projectName: String,
    val numberOfClients: Int,
    val numberOfEmployees: Int,
    val numberOfTasksDone: Int,
    val numberOfTasksOngoing: Int,
    val budgetAvailable: Double,
    val budgetSpent: Double
)
