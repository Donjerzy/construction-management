package com.construction.management.cm

import com.construction.management.cm.validator.Validator
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test


class ValidatorTest {

    @Test
    fun `empty string should be an invalid email`() {
        val validator = Validator()
        assertFalse(validator.isValidEmail(email = ""))
    }


}