package com.construction.management.cm.employeewagepayment

import com.construction.management.cm.employee.Employee
import jakarta.persistence.*
import java.util.*

@Entity(name = "employee_wage_payment")
class EmployeeWagePayment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    val id: Long = 0

    @Column(name = "period_start", nullable = false)
    var periodStart: Date = Date()

    @Column(name = "period_end", nullable = false)
    var periodEnd: Date = Date()

    @Column(name = "amount", nullable = false)
    var amount: Double = 0.0

    @Column(name = "transaction_date", nullable = false)
    val transactionDate: Date = Date()

    @Column(name = "note", nullable = true)
    var note: String? = null

    // Table references
    @ManyToOne
    @JoinColumn(name = "employee")
    var employee = Employee()

}