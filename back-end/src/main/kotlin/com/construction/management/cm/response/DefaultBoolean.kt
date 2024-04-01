package com.construction.management.cm.response

data class DefaultBoolean (
    val httpStatus: Int,
    val message: String,
    val bool: Boolean
)
