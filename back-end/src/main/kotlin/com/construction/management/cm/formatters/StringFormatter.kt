package com.construction.management.cm.formatters

import org.springframework.stereotype.Service

@Service
class StringFormatter {
    fun formatNames(name: String) : String {
        return name.split("\\W+".toRegex()).joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }
    }


}