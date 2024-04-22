package com.construction.management.cm.response

import com.construction.management.cm.dto.Overview

data class OverviewResponse(
    val httpStatus: Int,
    val message: String,
    val overview: Overview
)
