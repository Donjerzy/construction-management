package com.construction.management.cm.response

import com.construction.management.cm.dto.WageTypes

data class WageTypeResponse(
    val httpStatus: Int,
    val message: String,
    val wageTypes: MutableSet<WageTypes>
)
