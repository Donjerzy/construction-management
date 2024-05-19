package com.construction.management.cm.dto

data class GetTaskHistory(
    val date: String,
    val status: String,
    val user: String
)
