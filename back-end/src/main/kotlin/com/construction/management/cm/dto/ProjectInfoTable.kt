package com.construction.management.cm.dto

data class ProjectInfoTable (
    val creationDate: String,
    val status: String,
    val numberOfClients: Int,
    val completionDate: String,
    val numberOfEmployees: Int
)
