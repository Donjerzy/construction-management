package com.construction.management.cm.expensetype

import org.springframework.data.jpa.repository.JpaRepository

interface ExpenseTypeRepository: JpaRepository<ExpenseType, Long> {
}