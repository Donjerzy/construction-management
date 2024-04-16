package com.construction.management.cm.expense

import com.construction.management.cm.document.Document
import com.construction.management.cm.expensetype.ExpenseType
import com.construction.management.cm.unit.Unit
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import java.util.*

@Entity(name = "expense")
class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    val id: Long = 0

    @Column(name = "quantity", nullable = false)
    var quantity: Double = 0.0

    @Column(name = "cost_per_quantity", nullable = false)
    var costPerQuantity: Double = 0.0

    @Column(name = "note", length = 500)
    var note: String = "-"

    @Column(name = "date")
    val date: Date = Date()

    // Table references
    @ManyToOne
    @JoinColumn(name = "unit")
    var unit: Unit = Unit()

    @ManyToOne
    @JoinColumn(name = "expense_type")
    var expenseType = ExpenseType()

    @OneToMany(targetEntity = Document::class, mappedBy = "expense")
    var documents = mutableSetOf<Document>()


}