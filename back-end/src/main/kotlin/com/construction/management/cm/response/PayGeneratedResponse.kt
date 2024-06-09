package com.construction.management.cm.response

import com.construction.management.cm.dto.PayGenerated

data class PayGeneratedResponse (
    val httpStatus: Int,
    val message: String,
    val generated: PayGenerated
)
