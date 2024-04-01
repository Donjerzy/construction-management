package com.construction.management.cm.emailverification

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity(name="cm_email_verification")
class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    val id: Long = 0L

    @Column(name = "email", length = 100, nullable = false, unique = true)
    var email: String = "-"

    @Column(name = "code", length=4, nullable = false)
    var code: Int = 0



}