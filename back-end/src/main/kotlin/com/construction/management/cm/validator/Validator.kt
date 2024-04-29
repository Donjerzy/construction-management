package com.construction.management.cm.validator

import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeParseException


@Service
class Validator {

    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }

    fun isValidPassword(password: String): Boolean {
        var hasNoUpper = true
        var hasNoDigit = true
        var hasNoLower = true
        if(password.length < 6) {
            return false
        }
        for (x in password) {
            if(x.isDigit()) {
                hasNoDigit = false
                continue
            }
            if(x in 'A'.. 'Z') {
                hasNoUpper = false
                continue
            }
            if(x in 'a'.. 'z') {
                hasNoLower = false
                continue
            }
        }
        return !(hasNoLower || hasNoDigit || hasNoUpper)
    }


    fun isValidDate(date: String): Boolean {
        return try {
            LocalDate.parse(date)
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }






}