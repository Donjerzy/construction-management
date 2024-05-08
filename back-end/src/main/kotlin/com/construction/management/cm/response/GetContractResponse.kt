package com.construction.management.cm.response

data class GetContractResponse(
    val httpStatus: Int,
    val message: String,
    val contract: String
)
