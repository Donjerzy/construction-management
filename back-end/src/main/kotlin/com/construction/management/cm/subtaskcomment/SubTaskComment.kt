package com.construction.management.cm.subtaskcomment

import com.construction.management.cm.subtask.SubTask
import jakarta.persistence.*
import java.util.*

@Entity(name = "sub_task_comment")
class SubTaskComment {

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
    @JoinColumn(name = "sub_task")
    var subTask = SubTask()


}