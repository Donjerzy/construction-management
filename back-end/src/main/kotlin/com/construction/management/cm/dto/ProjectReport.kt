package com.construction.management.cm.dto

data class ProjectReport (
    val completeProjects: Int,
    val ongoingProjects: Int,
    val abandonedProjects: Int,
    val totalAvailableBudget: String,
    val projectBudgets: MutableList<ProjectBudgetResponse>
)
