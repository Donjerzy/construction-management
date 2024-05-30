package com.construction.management.cm.dto


data class AddTask (
    val title: String,
    val description: String,
    val project: Long,
    val employees: Set<Long>,
    val priority: String
)
