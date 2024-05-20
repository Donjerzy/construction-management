package com.construction.management.cm.expense

import com.construction.management.cm.auth.TokenService
import com.construction.management.cm.dto.AddExpense
import com.construction.management.cm.response.DefaultNa
import com.construction.management.cm.response.GetExpensesResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Controller
@RequestMapping("api/v1/expense")
class ExpenseController(
    private val service: ExpenseService,
    private val tokenService: TokenService
    ) {



    @PostMapping("/add", consumes = ["multipart/form-data"])
    fun addExpense(
        @RequestHeader("Authorization") header:String,
        @RequestParam("project") projectId: Long,
        @RequestParam("cost") cost: Double,
        @RequestParam("note") note: String? = null,
        @RequestParam("date") date: String,
        @RequestParam("document") document: MultipartFile? = null,
        @RequestParam("type") type: Long,
        @RequestParam("title") title: String
        ): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val addExpense = AddExpense(
            projectId = projectId,
            cost = cost,
            note = when {
                        note.isNullOrEmpty() -> null
                        note.isBlank() -> null
                        else -> note
                        },
            date = date,
            document = when {
                           document?.isEmpty == true -> null
                           document == null -> null
                           else -> document
                            },
            type = type,
            title = title
        )
        val message = service.addExpense(addExpense = addExpense, userEmail = userEmail!!)
        return ResponseEntity.status(200).body(
            DefaultNa (
                httpStatus = 200,
                message = message
            )
        )

    }


    @GetMapping("/all")
    fun getAllExpenses(
        @RequestHeader("Authorization") header:String,
                       @RequestParam("project") projectId: Long
        ):ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val expenses = service.getExpenses(userEmail = userEmail!!, projectId = projectId)
        return ResponseEntity.status(200).body(
            GetExpensesResponse(
                httpStatus = 200,
                message = "Expenses retrieved successfully",
                expenses = expenses
            )
        )
    }





}