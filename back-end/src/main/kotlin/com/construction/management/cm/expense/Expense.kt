package com.construction.management.cm.expense


import com.construction.management.cm.expensetype.ExpenseType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.util.*

@Entity(name = "expense")
class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    val id: Long = 0

    @Column(name = "cost", nullable = false)
    var cost: Double = 0.0

    @Column(name = "note", length = 500)
    var note: String? = null

    @Column(name = "date")
    val date: Date = Date()

    @Column(name = "document", nullable = true)
    val document: String? = null

    @Column(name = "expense_logger", nullable = false)
    val expenseLogger: String = ""

    @ManyToOne
    @JoinColumn(name = "expense_type")
    var expenseType = ExpenseType()

}