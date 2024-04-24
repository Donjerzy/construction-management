package com.construction.management.cm.client

import com.construction.management.cm.project.Project
import com.construction.management.cm.user.User
import jakarta.persistence.*

@Entity(name = "client")
class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    val id: Long = 0

    @Column(name = "name", length=50, nullable = false, unique = true)
    var name: String = "-"

    @Column(name = "type", length=20, nullable = false)
    var type: String = "-"

    @Column(name = "committed_amount")
    var committedAmount: Double = 0.0

    @Column(name = "invested_amount")
    var investedAmount: Double = 0.0

    // Table References
    @ManyToOne
    @JoinColumn(name = "project")
    var project = Project()

}