package com.construction.management.cm.dto

import java.util.*

data class PayEmployeeBody(
    val employeeId: Long,
    val amount: Double,
    val startDate: Date,
    val endDate: Date
)
