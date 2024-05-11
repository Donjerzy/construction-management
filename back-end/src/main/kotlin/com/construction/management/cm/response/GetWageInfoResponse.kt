package com.construction.management.cm.response

import com.construction.management.cm.dto.WageInfo

data class GetWageInfoResponse(
    val httpStatus: Int,
    val message: String,
    val wageInfo: WageInfo
)
