package com.construction.management.cm.expense

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ExpenseRepository: JpaRepository<Expense, Long> {
    @Query("select * from expense where project = :projectId order by date desc", nativeQuery = true)
    fun getProjectExpenses(projectId: Long): MutableList<Expense>

    @Query("select * from expense where lower(expense_logger) = :employeeId", nativeQuery = true)
    fun getEmployeeExpenses(employeeId: String): MutableList<Expense>

    @Query("select count(*) from expense where lower(expense_logger) = :employeeId and id = :expenseId", nativeQuery = true)
    fun isEmployeeExpense(employeeId: String, expenseId: Long): Int

    @Query("select coalesce(sum(coalesce(cost,0)),0) as total from expense where project = :projectId", nativeQuery = true)
    fun getProjectTotalExpense(projectId: Long): Double

    @Query("select coalesce(sum(coalesce(cost,0)),0) as total from expense where project = :projectId and expense_type = :type", nativeQuery = true)
    fun getProjectExpenseByType(projectId: Long, type: Long): Double


}