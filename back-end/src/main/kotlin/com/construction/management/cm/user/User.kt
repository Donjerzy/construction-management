package com.construction.management.cm.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.util.UUID

@Entity(name = "cm_user")
class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long = 0L

    @Column(name = "user_identifier", nullable = false, unique = true)
    var userIdentifier: UUID? = null

    @Column(name="email", nullable = false, unique = true, length=50)
    var email: String = "-"

    @Column(name = "first_name", nullable = false, length = 50)
    var firstName: String = "-"

    @Column(name = "surname", nullable = false, length = 50)
    var surname: String = "-"

    @Column(name = "password", nullable = false, length = 150)
    var password: String = "-"

    @Column(name = "user_role", nullable = false)
    var userRole:String = "-"
}