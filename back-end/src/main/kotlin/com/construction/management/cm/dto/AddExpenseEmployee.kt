package com.construction.management.cm.dto

import org.springframework.web.multipart.MultipartFile

data class AddExpenseEmployee(
    val projectId: String,
    val cost: Double,
    val note: String?,
    val date: String,
    val document: MultipartFile?,
    val type: Long,
    val title: String
)
