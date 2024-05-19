package com.construction.management.cm.dto

data class GetTaskComments(
    val commenter: String,
    val date: String,
    val comment: String
)
