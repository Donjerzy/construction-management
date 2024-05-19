package com.construction.management.cm.expensetype

import com.construction.management.cm.expense.Expense
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity(name = "expense_type")
class ExpenseType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    var id: Long = 0

    @Column(name = "name", nullable = false, unique = true)
    var name: String = "-"

    // Table references
    @OneToMany(targetEntity = Expense::class, mappedBy = "expenseType", cascade = [CascadeType.ALL])
    val expenses = mutableSetOf<Expense>()

}