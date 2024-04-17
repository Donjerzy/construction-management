package com.construction.management.cm.document

import com.construction.management.cm.documenttype.DocumentType
import com.construction.management.cm.expense.Expense
import jakarta.persistence.*
import java.util.*

@Entity(name = "document")
class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    val id: Long = 0

    @Column(name = "location", unique = true, nullable = false, length = 100)
    val location: String = "-"

    @Column(name = "date")
    val date: Date = Date()

    // Table references
    @ManyToOne
    @JoinColumn(name = "type")
    val type = DocumentType()

    @ManyToOne
    @JoinColumn(name = "expense")
    val expense = Expense()
}