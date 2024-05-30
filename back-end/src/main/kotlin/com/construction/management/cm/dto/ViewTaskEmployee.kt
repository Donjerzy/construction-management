package com.construction.management.cm.dto

data class ViewTaskEmployee (
    val taskId: Long,
    val title: String,
    val creationDate: String,
    val completionDate: String,
    val description: String,
    val status: String,
    val employees: MutableMap<Long, String>,
    val taskComments: MutableList<GetTaskComments>,
    val priority: String
)
