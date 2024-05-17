package com.construction.management.cm.dto

data class ProjectBudgetTable(
    val name: String,
    val status: String,
    val totalAmountCommitted: String,
    val totalAmountReceived: String,
    val totalAmountSpent: String,
    val totalAmountAvailable: String
)
