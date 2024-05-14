package com.construction.management.cm.projectstage

import com.construction.management.cm.task.Task
import jakarta.persistence.*

@Entity(name = "project_stage")
class ProjectStage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    val id: Long = 0

    @Column(name = "name")
    val name: String = "-"

    // Table references
//    @OneToMany(targetEntity = Task::class, mappedBy = "projectStage", cascade = [CascadeType.ALL])
//    val tasks = mutableSetOf<Task>()


}