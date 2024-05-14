package com.construction.management.cm.dto

data class GetExpectedInvestment (
    val client: String,
    val totalCommitted: String,
    val totalInvested: String,
    val remainingAmount: String
)
