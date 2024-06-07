package com.construction.management.cm.dto

data class AssignEmployees (
    val projectId: Long,
    val taskId: Long,
    val employees: MutableSet<Long>
)
