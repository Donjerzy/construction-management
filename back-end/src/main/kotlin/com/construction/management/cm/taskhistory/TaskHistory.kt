package com.construction.management.cm.taskhistory

import jakarta.persistence.*
import java.util.*

@Entity(name = "task_history")
class TaskHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    val id: Long = 0

    @Column(name = "task_id", nullable = false)
    var taskId: Long = 0

    @Column(name = "status", nullable = false)
    var status: String = ""

    @Column(name = "entry_date", nullable = false)
    var entryDate: Date = Date()

    @Column(name = "modifying_user", nullable = false)
    var user: String = ""

}