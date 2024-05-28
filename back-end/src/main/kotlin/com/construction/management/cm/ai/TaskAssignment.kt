package com.construction.management.cm.ai

import jakarta.persistence.*

@Entity(name = "task_assignment")
class TaskAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    val id: Long = 0

    @Column(name = "employee_id", nullable = false)
    val employee: Long = 0

    @Column(name = "task_id", nullable = false)
    val taskId: Long = 0

    @Column(name = "active_tasks", nullable = false)
    val activeTasks: Long = 0

    @Column(name = "average_completion", nullable = false)
    val averageCompletion:  Double = 0.0

    @Column(name = "time_taken", nullable = false)
    val timeTaken:  Double = 0.0

    @Column(name = "dataset", nullable = false)
    val dataSet: String = ""

}