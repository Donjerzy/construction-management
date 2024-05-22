package com.construction.management.cm.employeeexpense

import com.construction.management.cm.dto.AddExpense
import com.construction.management.cm.dto.AddExpenseEmployee
import com.construction.management.cm.dto.GetExpenses
import com.construction.management.cm.employee.EmployeeRepository
import com.construction.management.cm.exceptionhandler.CustomException
import com.construction.management.cm.expense.Expense
import com.construction.management.cm.expense.ExpenseRepository
import com.construction.management.cm.expense.ExpenseService
import com.construction.management.cm.expensetype.ExpenseTypeRepository
import com.construction.management.cm.formatters.StringFormatter
import com.construction.management.cm.project.ProjectRepository
import com.construction.management.cm.validator.Validator
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.util.*

@Service
class EmployeeExpenseService(
    private val projectRepository: ProjectRepository,
    private val employeeRepository: EmployeeRepository,
    private val expenseTypeRepository: ExpenseTypeRepository,
    private val validator: Validator,
    private val formatter: StringFormatter,
    private val expenseService: ExpenseService,
    private val expenseRepository: ExpenseRepository
) {
    fun addExpense(addExpense: AddExpenseEmployee, userId: Long): String {
        when (addExpenseValidations(
            userId = userId,
            addExpense = addExpense
        )) {
            "invalid-project" -> throw CustomException("invalid-project", null)
            "not-project-owner" -> throw CustomException("not-project-owner", null)
            "invalid-expense-type" -> throw CustomException("invalid-expense-type", null)
            "cost-lz" ->  throw CustomException("cost-lz", null)
            "invalid-date" -> throw CustomException("invalid-date",null)
            "invalid-title" -> throw CustomException("invalid-title", null)
        }
        val project = projectRepository.getProjectByUuid(UUID.fromString(addExpense.projectId))
        project.totalBudgetAmountSpent += addExpense.cost
        projectRepository.save(project)

        val expense = Expense()
        expense.cost = addExpense.cost
        expense.note = when{
            addExpense.note.isNullOrEmpty() -> null
            addExpense.note.isBlank() -> null
            else -> addExpense.note
        }
        expense.date = formatter.toDate(addExpense.date)
        expense.title = addExpense.title
        expense.document = when(addExpense.document) {
            null -> null
            else -> expenseService.saveExpenseDocument(
                expenseDoc = addExpense.document,
                project = UUID.fromString(addExpense.projectId),
                date = addExpense.date,
                title = addExpense.title
            )
        }
        expense.expenseLogger = "E$userId"
        expense.expenseType = expenseTypeRepository.findById(addExpense.type).get()
        expense.project = projectRepository.getProjectByUuid(UUID.fromString(addExpense.projectId))
        expenseRepository.save(expense)
        return "Expense added successfully"
    }

    fun addExpenseValidations(
        userId: Long,
        addExpense: AddExpenseEmployee
    ): String {
        if (projectRepository.projectUuidExists(UUID.fromString(addExpense.projectId)) <=0 ) {
            return "invalid-project"
        }
        if (employeeRepository.findById(userId).get().project.projectId != UUID.fromString(addExpense.projectId)) {
            return "invalid-employee"
        }
        if (!expenseTypeRepository.findById(addExpense.type).isPresent) {
            return "invalid-expense-type"
        }
        if (addExpense.cost <= 0) {
            return "cost-lz"
        }
        if (!validator.isValidDate(addExpense.date)) {
            return "invalid-date"
        }
        if (addExpense.title.isEmpty() || addExpense.title.isBlank()) {
            return "invalid-title"
        }
        return "ok"
    }

    fun getEmployeeExpenses(userId: Long): MutableList<GetExpenses> {
       return expensesToGetExpenses(expenseRepository.getEmployeeExpenses(employeeId = "e$userId"))
    }

    private fun expensesToGetExpenses(employeeExpenses: MutableList<Expense>): MutableList<GetExpenses> {
        val result = mutableListOf<GetExpenses>()
        for (expense in employeeExpenses) {
            result.add(
                GetExpenses(
                    expenseId = expense.id,
                    addedBy = "You",
                    cost = formatter.doubleToString(expense.cost),
                    date = formatter.timestampToString(expense.date),
                    hasDocument = expense.document != null,
                    note = expense.note ?: "n/a",
                    title = expense.title,
                    type = expense.expenseType.name
                )
            )
        }
        return  result
    }

    fun getDocument(userId: Long, expenseId: Long): String {
        when(getDocumentValidations(userId = userId, expenseId = expenseId)) {
            "invalid-employee" -> throw CustomException("invalid-employee", null)
            "no-document" -> throw CustomException("no-document", null)
        }
        val actualDocument = File(expenseRepository.findById(expenseId).get().document!!)
        val inputStream = FileInputStream(actualDocument)
        val byteArray = inputStream.readAllBytes()
        return Base64.getEncoder().encodeToString(byteArray)
    }

    fun getDocumentValidations(userId: Long, expenseId: Long): String {
        if (expenseRepository.isEmployeeExpense(
                employeeId = "e$userId",
                expenseId = expenseId
        ) <=0 ) {
            return "invalid-employee"
        }
        val expense = expenseRepository.findById(expenseId).get()
        if (expense.document == null) {
            return "no-document"
        }
        return "ok"
    }

}