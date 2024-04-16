package com.construction.management.cm.project

import com.construction.management.cm.client.Client
import com.construction.management.cm.user.User
import jakarta.persistence.*

@Entity(name = "com/construction/management/cm/project")
class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    val id: Long = 0

    @Column(name="name", nullable = false, unique = true, length = 50)
    var name:String = "-"

    @Column(name = "committed_budget", nullable = false)
    var committedBudgetAmount:Double = 0.0

    @Column(name = "total_budget_amount_received", nullable = false)
    var totalBudgetAmountReceived:Double = 0.0

    @Column(name = "total_budget_amount_spent", nullable = false)
    var totalBudgetAmountSpent:Double = 0.0


    // Table References
    @OneToMany(mappedBy = "project", targetEntity = Client::class, cascade = [CascadeType.ALL])
    var clients = emptySet<Client>()

    @ManyToOne
    @JoinColumn(name = "project_manager")
    var projectManager = User()






}