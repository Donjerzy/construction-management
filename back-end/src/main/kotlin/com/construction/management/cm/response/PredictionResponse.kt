package com.construction.management.cm.response

data class PredictionResponse (
    val httpStatus: Int,
    val message: String,
    val minutes: Double
)
