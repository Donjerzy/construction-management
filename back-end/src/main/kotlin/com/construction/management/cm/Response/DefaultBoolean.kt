package com.construction.management.cm.Response

data class DefaultBoolean (
    val httpStatus: Int,
    val message: String,
    val bool: Boolean
)
