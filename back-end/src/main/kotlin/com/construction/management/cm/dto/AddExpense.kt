package com.construction.management.cm.dto

import org.springframework.web.multipart.MultipartFile
import java.util.Date

data class AddExpense (
    val projectId: Long,
    val cost: Double,
    val note: String?,
    val date: String,
    val document: MultipartFile?,
    val type: Long,
    val title: String
)
