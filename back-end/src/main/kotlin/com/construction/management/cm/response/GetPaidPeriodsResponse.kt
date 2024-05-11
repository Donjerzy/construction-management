package com.construction.management.cm.response

import com.construction.management.cm.dto.PaidPeriods

data class GetPaidPeriodsResponse(
    val httpStatus: Int,
    val message: String,
    val periods: MutableSet<PaidPeriods>
)
