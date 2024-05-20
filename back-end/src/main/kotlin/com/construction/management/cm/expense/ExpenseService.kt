package com.construction.management.cm.expense

import com.construction.management.cm.Runner.Runner
import com.construction.management.cm.dto.AddExpense
import com.construction.management.cm.exceptionhandler.CustomException
import com.construction.management.cm.expensetype.ExpenseType
import com.construction.management.cm.expensetype.ExpenseTypeRepository
import com.construction.management.cm.formatters.StringFormatter
import com.construction.management.cm.project.Project
import com.construction.management.cm.project.ProjectRepository
import com.construction.management.cm.user.UserService
import com.construction.management.cm.validator.Validator
import jakarta.persistence.Column
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.*

@Service
class ExpenseService(
    private val repository: ExpenseRepository,
    private val userService: UserService,
    private val projectRepository: ProjectRepository,
    private val expenseTypeRepository: ExpenseTypeRepository,
    private val validator: Validator,
    private val formatter: StringFormatter,
    private val runner: Runner) {

    fun addExpense(addExpense: AddExpense, userEmail: String): String {
        when (addExpenseValidations(
            projectOwner = userService.getUserId(userEmail)!!,
            addExpense = addExpense
        )) {
            "invalid-project" -> throw CustomException("invalid-project", null)
            "not-project-owner" -> throw CustomException("not-project-owner", null)
            "invalid-expense-type" -> throw CustomException("invalid-expense-type", null)
            "cost-lz" ->  throw CustomException("cost-lz", null)
            "invalid-date" -> throw CustomException("invalid-date",null)
            "invalid-title" -> throw CustomException("invalid-title", null)
        }
        val project = projectRepository.findById(addExpense.projectId).get()
        project.totalBudgetAmountSpent += addExpense.cost
        projectRepository.save(project)

        val expense = Expense()
        expense.cost = addExpense.cost
        expense.note = addExpense.note
        expense.date = formatter.toDate(addExpense.date)
        expense.title = addExpense.title
        expense.document = when(addExpense.document) {
            null -> null
            else -> saveExpenseDocument(
                expenseDoc = addExpense.document,
                project = projectRepository.findById(addExpense.projectId).get().projectId,
                date = addExpense.date,
                title = addExpense.title
            )
        }
        expense.expenseLogger = "O${userService.getUserId(userEmail)}"
        expense.expenseType = expenseTypeRepository.findById(addExpense.type).get()
        expense.project = projectRepository.findById(addExpense.projectId).get()
        repository.save(expense)
        return "Expense added successfully"
    }

    fun saveExpenseDocument(expenseDoc: MultipartFile, project: UUID, date: String, title: String): String {
        val expenseDirectoryPath = "expense_docs"
        val validTitle = title.replace("\\s".toRegex(), "")
        val fileName = "$project-$validTitle-$date"
        val expenseDirectory = File(expenseDirectoryPath)
        if(!expenseDirectory.exists()) {
            expenseDirectory.mkdir()
        }
        val fileSaveLocation = File(expenseDirectory.absolutePath, fileName)
        expenseDoc.transferTo(fileSaveLocation)
        return fileSaveLocation.absolutePath
    }

    fun addExpenseValidations(projectOwner: Long, addExpense: AddExpense): String {
        if (!projectRepository.findById(addExpense.projectId).isPresent) {
            return "invalid-project"
        }
        val fetchedOwner = projectRepository.findById(addExpense.projectId).get().projectManager.id
        if (fetchedOwner != projectOwner) {
            return "not-project-owner"
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
}