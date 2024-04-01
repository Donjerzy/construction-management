package com.construction.management.cm.dto

data class SignUp(
    val verificationCode: Int,
    val email: String,
    val firstName: String,
    val surname: String,
    val password: String
)
