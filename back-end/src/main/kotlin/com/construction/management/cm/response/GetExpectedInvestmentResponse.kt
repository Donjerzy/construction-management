package com.construction.management.cm.response

data class GetExpectedInvestmentResponse(
    val httpStatus: Int,
    val message: String,
    val expectedInvestment: String
)
