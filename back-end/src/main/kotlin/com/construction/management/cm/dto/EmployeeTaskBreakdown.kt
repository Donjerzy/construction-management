package com.construction.management.cm.dto

data class EmployeeTaskBreakdown(
    val done: Int,
    val ongoing: Int,
    val doneOnTime: Int,
    val donePastTime: Int
)
