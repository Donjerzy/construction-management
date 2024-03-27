package com.construction.management.cm.User

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity(name = "cm_user")
class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long = 0L

    @Column(name="email", nullable = false, unique = true, length=50)
    val email: String = "-"

    @Column(name = "first_name", nullable = false, length = 50)
    val firstName: String = "-"

    @Column(name = "surname", nullable = false, length = 50)
    val surname: String = "-"

    @Column(name = "password", nullable = false, length = 150)
    val password: String = "-"
}