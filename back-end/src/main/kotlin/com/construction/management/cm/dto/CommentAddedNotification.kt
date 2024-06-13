package com.construction.management.cm.dto

data class CommentAddedNotification (
    val toAlert: MutableMap<String, String>,
    val task: String,
    val commenter: String,
    val date: String,
    val comment: String,
    val project: String
)
