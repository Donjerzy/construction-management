package com.construction.management.cm.response

import com.construction.management.cm.dto.Employees


data class GetEmployeesResponse(
    val httpStatus: Int,
    val message: String,
    val employees: MutableSet<Employees>
)