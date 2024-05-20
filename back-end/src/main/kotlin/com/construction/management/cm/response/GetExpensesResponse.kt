package com.construction.management.cm.response

import com.construction.management.cm.dto.GetExpenses

data class GetExpensesResponse(
    val httpStatus: Int,
    val message: String,
    val expenses: MutableList<GetExpenses>
)
