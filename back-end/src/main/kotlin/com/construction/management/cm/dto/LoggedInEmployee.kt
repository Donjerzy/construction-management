package com.construction.management.cm.dto

data class LoggedInEmployee(
    val token: String,
    val firstName: String,
    val project: String
)
