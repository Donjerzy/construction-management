package com.construction.management.cm.dto

data class ViewTask (
    val taskId: Long,
    val title: String,
    val creationDate: String,
    val completionDate: String,
    val description: String,
    val status: String,
    val priority: String,
    val employees: MutableMap<Long, String>,
    val taskHistory: MutableList<GetTaskHistory>,
    val taskComments: MutableList<GetTaskComments>
)
