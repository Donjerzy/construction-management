package com.construction.management.cm.project

import com.construction.management.cm.client.Client
import com.construction.management.cm.task.Task
import com.construction.management.cm.user.User
import jakarta.persistence.*
import java.time.LocalDate
import java.util.*
import kotlin.math.absoluteValue

@Entity(name = "project")
class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    val id: Long = 0

    @Column(name= "project_id", nullable = false, unique = true)
    var projectId: UUID = UUID.randomUUID()

    @Column(name="name", nullable = false, unique = true, length = 50)
    var name:String = "-"

    @Column(name = "committed_budget", nullable = false)
    var committedBudgetAmount:Double = 0.0

    @Column(name = "total_budget_amount_received", nullable = false)
    var totalBudgetAmountReceived:Double = 0.0

    @Column(name = "total_budget_amount_spent", nullable = false)
    var totalBudgetAmountSpent:Double = 0.0

    @Column(name = "status", nullable = false, length = 20)
    var status: String = ProjectStatusEnum.ONGOING.name

    @Column(name = "creation_date", nullable = false)
    var creationDate: Date = Date()

    @Column(name = "completion_date", nullable = true)
    var completionDate: Date? = null

    // Table References
    @OneToMany(mappedBy = "project", targetEntity = Client::class, cascade = [CascadeType.ALL])
    var clients = mutableSetOf<Client>()

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "project_manager")
    var projectManager = User()

    @OneToMany(mappedBy = "project", targetEntity = Task::class, cascade = [CascadeType.ALL])
    var tasks = mutableListOf<Task>()

}