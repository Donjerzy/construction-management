package com.construction.management.cm.User

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType

@Entity(name = "user")
class User {

    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long = 0L

    @Column(name = "first_name", nullable = false, length = 50)
    val firstName: String = "-"

    @Column(name = "surname", nullable = false, length = 50)
    val surname: String = "-"

    @Column(name = "password", nullable = false, length = 150)
    val password: String = "-"

}