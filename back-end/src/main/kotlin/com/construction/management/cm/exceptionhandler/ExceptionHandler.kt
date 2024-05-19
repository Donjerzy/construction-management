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

            "client-doesn't-exist" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Client Doesn't Exist"
                )
            )

            "invalid-credentials" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Invalid credentials"
                )
            )

            "client-with-provided-name-exists" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Client With Provided Name Exists"
                )
            )

            "committed-amount-lz" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Committed Amount Cannot be Less than Zero"
                )
            )

            "invested-amount-lz" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Invested Amount Cannot be Less than Zero"
                )
            )

            "committed-invested-error" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Invested amount cannot be greater than what has been committed"
                )
            )

            "project-doesn't-exists" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Project Doesn't Exist"
                )
            )

            "invalid-client-type" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Invalid Client Type"
                )
            )

            "client-exists" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Client Already Exists"
                )
            )

            "not-project-owner" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Requester is not the project owner"
                )
            )

            "invalid-first-name" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Invalid first name"
                )
            )

            "invalid-last-name" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Invalid last name"
                )
            )

            "employee-has-no-contract" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Employee has no contract"
                )
            )

            "employee-already-has-contract" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Employee already has a contract"
                )
            )

            "first-name-empty" ->  ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "First name cannot be empty"
                )
            )

            "last-name-empty" ->  ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Last name cannot be empty"
                )
            )

            "employee-already-in-project" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Employee is already in the project"
                )
            )

            "employee-doesn't-exist" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Employee doesn't exist"
                )
            )

            "employee-not-in-project" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Employee is not in the project"
                )
            )

            "invalid-employee-type" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Employee type does not exist"
                )
            )

            "invalid-title" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Title cannot be empty"
                )
            )

            "invalid-employee" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Invalid employee id provided"
                )
            )

            "invalid-move-action" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Invalid task move action"
                )
            )

            "task-doesn't-exist" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Invalid task provided"
                )
            )

            "invalid-wage-type" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Wage type does not exist"
                )
            )

            "invalid-date" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Invalid date format kindly adhere to yyyy-mm-dd"
                )
            )

            "wage-lz" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Wage cannot be less than zero"
                )
            )

            "invalid-amount" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Amount provided is invalid"
                )
            )

            "invalid-project" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Invalid project"
                )
            )

            "invalid-date-period" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Date period entered is invalid"
                )
            )

            "invalid-password" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Invalid Password, password must be at least 6 characters in length with at least 1 uppercase letter, 1 lowercase letter and 1 digit."
                )
            )

            "email-empty" ->  ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Email cannot be empty"
                )
            )

            "invalid-email" ->  ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Email provided is invalid"
                )
            )

            "empty-comment" -> ResponseEntity.status(400).body(
                ExceptionResponse(
                    httpStatus = 400,
                    message = "Comment cannot be empty"
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