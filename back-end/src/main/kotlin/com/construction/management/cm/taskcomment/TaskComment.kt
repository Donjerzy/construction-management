package com.construction.management.cm.taskcomment

import com.construction.management.cm.task.Task
import jakarta.persistence.*
import java.util.Date

@Entity(name = "task_comment")
class TaskComment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    val id : Long = 0

    @Column(name = "comment", length = 50, nullable = false)
    val comment: String = "-"

    @Column(name = "author_first_name", length = 50, nullable = false)
    val authorFirstName: String = "-"

    @Column(name = "author_surname", length = 50, nullable = false)
    val authorSurname: String = "-"

    @Column(name = "date")
    val date: Date = Date()

    // Table references
    @ManyToOne
    @JoinColumn(name = "task")
    var task = Task()

}