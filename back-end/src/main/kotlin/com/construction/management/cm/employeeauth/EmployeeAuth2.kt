package com.construction.management.cm.employeeauth

import jakarta.persistence.*

@Entity(name = "cm_employee")
class EmployeeAuth2 {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    val id: Long = 0

    @Column(name = "email", nullable = false, unique = true)
    val email: String = ""

    @Column(name = "first_name", nullable = false)
    val firstName: String = ""

    @Column(name = "last_name", nullable = false)
    val lastName: String = ""

    @Column(name = "password")
    val password: String = ""

}