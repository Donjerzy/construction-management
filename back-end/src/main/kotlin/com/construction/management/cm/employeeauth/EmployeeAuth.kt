package com.construction.management.cm.employeeauth

import jakarta.persistence.*

@Entity(name = "employee_auth")
class EmployeeAuth {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    val id: Long = 0

    @Column(name = "token", unique = true, nullable = true)
    val token: String? = null
}