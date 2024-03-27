package com.construction.management.cm.Response

data class UserExists (
    val httpStatus: Int,
    val message: String,
    val userExists: Boolean
)
