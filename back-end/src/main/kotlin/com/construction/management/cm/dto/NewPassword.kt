package com.construction.management.cm.dto

data class NewPassword (
    val email: String,
    val password: String,
    val code: Int
)