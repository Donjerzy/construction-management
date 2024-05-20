package com.construction.management.cm.dto

data class GetExpenses (
    val expenseId: Long,
    val cost: String,
    val date: String,
    val hasDocument: Boolean,
    val addedBy: String,
    val note: String,
    val title: String,
    val type: String
)
