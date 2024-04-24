package com.construction.management.cm.dto

data class ProjectClient(
    val id:Long,
    val committedAmount: String,
    val investedAmount: String,
    val name: String,
    val type: String
)
