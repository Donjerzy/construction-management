package com.construction.management.cm.dto

data class GetWageHistory (
    val note: String,
    val amount: String,
    val periodStart: String,
    val periodEnd: String,
    val transactionDate: String
)
