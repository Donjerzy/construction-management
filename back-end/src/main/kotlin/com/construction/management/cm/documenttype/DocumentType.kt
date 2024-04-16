package com.construction.management.cm.documenttype

import com.construction.management.cm.document.Document
import com.construction.management.cm.expense.Expense
import jakarta.persistence.*

@Entity(name = "document_type")
class DocumentType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    val id: Long = 0

    @Column(name = "name", unique = true, nullable = false, length = 50)
    val name: String = "-"

    // Table references
    @OneToMany(targetEntity = Document::class, mappedBy = "type", cascade = [CascadeType.ALL])
    val documents = mutableSetOf<Document>()
}