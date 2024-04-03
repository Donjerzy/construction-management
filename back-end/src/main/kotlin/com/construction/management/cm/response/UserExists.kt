package com.construction.management.cm.response

data class UserExists (
    val httpStatus: Int,
    val message: String,
    val userExists: Boolean
)
