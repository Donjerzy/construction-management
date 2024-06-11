package com.construction.management.cm.response

import com.construction.management.cm.dto.Member

data class MemberResponse (
    val httpStatus: Int,
    val message: String,
    val info: Member
)
