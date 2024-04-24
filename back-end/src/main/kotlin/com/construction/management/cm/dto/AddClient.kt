package com.construction.management.cm.dto

data class AddClient(
    val name: String,
    val type: String,
    val project:Long,
    val investedAmount: Double,
    val committedAmount: Double
)
