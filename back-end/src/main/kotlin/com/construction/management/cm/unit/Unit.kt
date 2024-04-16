package com.construction.management.cm.unit

import com.construction.management.cm.expense.Expense
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity(name = "unit")
class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    val id: Long = 0

    @Column(name = "name", unique = true, nullable = false, length = 50)
    val name: String = "-"

    // Table references
    @OneToMany(targetEntity = Expense::class, mappedBy = "unit", cascade = [CascadeType.ALL])
    val expenses = mutableSetOf<Expense>()

}