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
    val periodStart: Date = Date()

    @Column(name = "period_end", nullable = false)
    val periodEnd: Date = Date()

    @Column(name = "amount", nullable = false)
    val amount: Double = 0.0

    @Column(name = "transaction_date", nullable = false)
    val transactionDate: Date = Date()

    // Table references
    @ManyToOne
    @JoinColumn(name = "employee")
    val employee = Employee()











}