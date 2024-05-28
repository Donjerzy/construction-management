package com.construction.management.cm.ai

import jakarta.persistence.*

@Entity(name = "task_assignment_model")
class BestTaskAssignmentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    val id: Long = 0

    @Column(name = "path", nullable = false)
    val path: String = ""

    @Column(name = "score", nullable = false)
    val score: Double = 0.0

}