package com.construction.management.cm.ExceptionHandler

import com.construction.management.cm.Response.ExceptionResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler
    fun handleCustomException(exception: CustomException): ResponseEntity<Any> {
        return when (exception.message) {
            "user-exists-signup" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "User Already Exists"
                )
            )

            "cannot-send-email" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 500,
                    message = "Unable to send email to user due to ${exception.additionalInfo}"
                )
            )

            else -> ResponseEntity.status(500).body(ExceptionResponse(
                httpStatus = 500,
                message = "Internal Server Error"
            ))
        }
    }


}