package com.construction.management.cm.response

data class ExpenseDocumentResponse (
    val httpStatus: Int,
    val message: String,
    val document: String
)
