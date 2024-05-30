package com.construction.management.cm.task

import com.construction.management.cm.employee.Employee
import com.construction.management.cm.project.Project
import com.construction.management.cm.projectstage.ProjectStage
import com.construction.management.cm.subtask.SubTask
import com.construction.management.cm.taskcomment.TaskComment
import jakarta.persistence.*
import java.util.*

@Entity(name = "task")
class Task {

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

    @Column(name = "priority", nullable = false)
    var priority: String = ""

    @Column(name = "completion_date")
    var completionDate: Date? = null

    // Table references
    @ManyToOne
    @JoinColumn(name = "project")
    var project = Project()

//    @ManyToOne
//    @JoinColumn(name = "stage")
//    var projectStage = ProjectStage()

    @ManyToMany(targetEntity = Employee::class)
    @JoinTable(
        name = "employee_task",
        joinColumns = [JoinColumn(name = "task")],
        inverseJoinColumns = [JoinColumn(name = "employee")]
    )
    var employees = mutableSetOf<Employee>()

    @OneToMany(targetEntity = TaskComment::class, mappedBy = "task", cascade = [CascadeType.ALL])
    var comments = mutableSetOf<TaskComment>()

    @OneToMany(targetEntity = SubTask::class, mappedBy = "task", cascade = [CascadeType.ALL])
    var subTasks = mutableSetOf<SubTask>()

}