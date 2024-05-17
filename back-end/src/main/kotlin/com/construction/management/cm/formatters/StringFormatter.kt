package com.construction.management.cm.formatters

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date

@Service
class StringFormatter {


    @Autowired
    private lateinit var encoder: PasswordEncoder

    fun formatNames(name: String) : String {
        return name.split("\\W+".toRegex()).joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }
    }

    fun toDate(text: String): Date {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        return formatter.parse(text)
    }

    fun toPassword(text: String) : String {
        return encoder.encode(text)
    }


    fun doubleToString(value: Double): String {
        return String.format("%.0f", value)
    }

    fun doubleToStringCommaSeparated(value: Double): String {
        val decimalFormat = DecimalFormat("#,###")
        return decimalFormat.format(value)
    }

    fun timestampToString(date: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        return dateFormat.format(date)
    }



}