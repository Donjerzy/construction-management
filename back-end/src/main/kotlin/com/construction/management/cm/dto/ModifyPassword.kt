package com.construction.management.cm.dto

data class ModifyPassword(
    val employeeId: Long,
    val newPassword: String
)
