package com.construction.management.cm.response

import com.construction.management.cm.dto.GetExpectedInvestment

data class GetExpectedInvestmentResponse(
    val httpStatus: Int,
    val message: String,
    val expectedInvestment: GetExpectedInvestment
)
