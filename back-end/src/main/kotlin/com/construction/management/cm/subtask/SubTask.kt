package com.construction.management.cm.subtask

import com.construction.management.cm.employee.Employee
import com.construction.management.cm.subtaskcomment.SubTaskComment
import com.construction.management.cm.task.Task
import jakarta.persistence.*
import java.util.*

@Entity(name = "sub_task")
class SubTask {

    @Id
    @GeneratedValue
    @Column(name = "id")
    val id: Long = 0

    @Column(name = "name", nullable = false, length = 50)
    var name: String = "-"

    @Column(name = "description", nullable = false, length = 500)
    var description: String = "-"

    @Column(name = "status")
    var status: String = "-"

    @Column(name = "creation_date")
    var creationDate: Date = Date()

    @Column(name = "completion_date")
    var completionDate: Date = Date()

    // Table references
    @ManyToOne
    @JoinColumn(name = "task")
    var task = Task()

    @ManyToOne
    @JoinColumn(name = "employee")
    var employee = Employee()

    @OneToMany(targetEntity = SubTaskComment::class, mappedBy = "subTask", cascade = [CascadeType.ALL])
    var comments = mutableSetOf<SubTaskComment>()



}