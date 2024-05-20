package com.construction.management.cm.expense


import com.construction.management.cm.expensetype.ExpenseType
import com.construction.management.cm.project.Project
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

    @Column(name = "title", nullable = false)
    var title: String = ""

    @Column(name = "note", length = 500)
    var note: String? = null

    @Column(name = "date")
    var date: Date = Date()

    @Column(name = "document", nullable = true)
    var document: String? = null

    @Column(name = "expense_logger", nullable = false)
    var expenseLogger: String = ""

    @ManyToOne
    @JoinColumn(name = "expense_type")
    var expenseType = ExpenseType()

    @ManyToOne
    @JoinColumn(name = "project")
    var project = Project()

}