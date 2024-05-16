package com.construction.management.cm.employeeauth

import jakarta.persistence.*

@Entity(name = "employee_auth")
class EmployeeAuth {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    @Column(name = "token", unique = true, nullable = true)
    var token: String? = null
}