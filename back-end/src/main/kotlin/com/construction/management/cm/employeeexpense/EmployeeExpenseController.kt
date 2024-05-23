package com.construction.management.cm.employeeexpense

import com.construction.management.cm.dto.AddExpense
import com.construction.management.cm.dto.AddExpenseEmployee
import com.construction.management.cm.employeeauth.EmployeeAuthService
import com.construction.management.cm.response.DefaultNa
import com.construction.management.cm.response.ExpenseDocumentResponse
import com.construction.management.cm.response.GetExpensesResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

@Controller
@RequestMapping("api/v1/employee/expense")
class EmployeeExpenseController(
    private val service: EmployeeExpenseService,
    private val authService: EmployeeAuthService
) {

    @PostMapping("/add", consumes = ["multipart/form-data"])
    fun addExpense(
        @RequestHeader(name = "Cmt") header: String,
        @RequestParam("project") projectId: String,
        @RequestParam("cost") cost: Double,
        @RequestParam("note") note: String? = null,
        @RequestParam("date") date: String,
        @RequestParam("document") document: MultipartFile? = null,
        @RequestParam("type") type: Long,
        @RequestParam("title") title: String
    ): ResponseEntity<Any> {
        val userId = authService.validateRequestToken(token = header)
        val addExpense = AddExpenseEmployee(
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
        val message = service.addExpense(addExpense = addExpense, userId = userId)
        return ResponseEntity.status(200).body(
            DefaultNa(
                httpStatus = 200,
                message = message
            )
        )
    }
    @GetMapping("/all")
    fun getEmployeeExpenses(
        @RequestHeader(name = "Cmt") header: String,
    ): ResponseEntity<Any> {
        val userId = authService.validateRequestToken(token = header)
        val expenses = service.getEmployeeExpenses(userId = userId)
        return ResponseEntity.status(200).body(GetExpensesResponse(
            httpStatus = 200,
            message = "Expenses retrieved successfully",
            expenses = expenses
        ))
    }

    @GetMapping("/document")
    fun getExpenseDocument(
        @RequestHeader(name = "Cmt") header: String,
        @RequestParam("expenseId") expenseId: Long
    ): ResponseEntity<Any> {
        val userId = authService.validateRequestToken(token = header)
        val document = service.getDocument(userId = userId, expenseId = expenseId)
        return ResponseEntity.status(200).body(
            ExpenseDocumentResponse (
                httpStatus = 200,
                message = "Document retrieved successfully",
                document = document
            )
        )
    }


}