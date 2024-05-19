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

    @Column(name = "comment", length = 1000, nullable = false)
    var comment: String = "-"

    @Column(name = "comment_owner", nullable = false)
    var commentUserId: String = ""

    @Column(name = "author_first_name", length = 50, nullable = false)
    var authorFirstName: String = "-"

    @Column(name = "author_surname", length = 50, nullable = false)
    var authorSurname: String = "-"

    @Column(name = "date")
    val date: Date = Date()

    // Table references
    @ManyToOne
    @JoinColumn(name = "task")
    var task = Task()

}