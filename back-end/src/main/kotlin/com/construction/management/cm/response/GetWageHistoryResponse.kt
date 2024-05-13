package com.construction.management.cm.response

import com.construction.management.cm.dto.GetWageHistory

data class GetWageHistoryResponse (
    val httpStatus: Int,
    val message: String,
    val wageHistory: MutableList<GetWageHistory>
)
