package com.construction.management.cm.expense

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ExpenseRepository: JpaRepository<Expense, Long> {
    @Query("select * from expense where project = :projectId", nativeQuery = true)
    fun getProjectExpenses(projectId: Long): MutableList<Expense>
}