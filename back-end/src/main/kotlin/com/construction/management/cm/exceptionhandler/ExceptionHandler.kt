package com.construction.management.cm.exceptionhandler

import com.construction.management.cm.response.ExceptionResponse
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

            "not-project-owner" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Requester is not the project owner"
                )
            )

            "project-exists" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Project Already Exists"
                )
            )

            "auth-user-na" -> ResponseEntity.status(401).body(
                ExceptionResponse(
                    httpStatus = 401,
                    message = "Unauthorized"
                )
            )

            "forgot-password-non-existent-user" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "User Does Not Exist"
                )
            )

            "cannot-send-email" -> ResponseEntity.status(500).body(
                ExceptionResponse(
                    httpStatus = 500,
                    message = "Unable to send email to user due to ${exception.additionalInfo}"
                )
            )

            "invalid-verification-code" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Invalid Verification Code"
                    )
            )

            "code-not-verified" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Unverified Verification Code"
                )
            )

            "persist-sign-up-error" -> ResponseEntity.status(500).body(
                ExceptionResponse(
                    httpStatus = 500,
                    message = "Could not Persist Correct User Sign Up Information"
                )
            )

            else -> ResponseEntity.status(500).body(ExceptionResponse(
                httpStatus = 500,
                message = "Internal Server Error"
            ))
        }
    }


}