package com.construction.management.cm.dto

data class AddEmployee (
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val wage: Double,
    val joinDate: String,
    val employeeType: Long,
    val wageType: Long,
    val project: Long
)