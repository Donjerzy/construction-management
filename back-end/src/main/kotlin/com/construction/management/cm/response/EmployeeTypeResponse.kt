package com.construction.management.cm.response

import com.construction.management.cm.dto.EmployeeTypes

data class EmployeeTypeResponse (
    val httpStatus: Int,
    val message: String,
    val employeeTypes: MutableSet<EmployeeTypes>
)