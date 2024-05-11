package com.construction.management.cm.employee

import com.construction.management.cm.employeetype.EmployeeType
import com.construction.management.cm.employeewagepayment.EmployeeWagePayment
import com.construction.management.cm.project.Project
import com.construction.management.cm.subtask.SubTask
import com.construction.management.cm.task.Task
import com.construction.management.cm.wagetype.WageType
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import java.util.*

@Entity(name = "employee")
class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    val id: Long = 0

    @Column(name = "first_name", nullable = false, length = 50)
    var firstName: String = "-"

    @Column(name = "last_name", nullable = false, length = 50)
    var lastName: String = "-"

    @Column(name = "email", nullable = false, length = 50)
    var email: String = "-"

    @Column(name = "password", nullable = false, length = 250)
    var password: String = "-"

    @Column(name = "wage")
    var wage: Double = 0.0

    @Column(name = "join_date", nullable = false)
    var joinDate: Date = Date()

    @Column(name = "contract", nullable = true, unique = true)
    var contract: String? = null

    @Column(name = "wage_paid")
    var wagesPaid: Double = 0.0

    // Table references
    @ManyToOne
    @JoinColumn(name="employee_type")
    var employeeType: EmployeeType = EmployeeType()

    @ManyToOne
    @JoinColumn(name = "wage_type")
    var wageType: WageType = WageType()

    @ManyToMany(mappedBy = "employees")
    var tasks = mutableSetOf<Task>()

    @OneToMany(targetEntity = SubTask::class, mappedBy = "employee", cascade = [CascadeType.ALL])
    var subTasks = mutableSetOf<SubTask>()

    @OneToMany(targetEntity = EmployeeWagePayment::class, mappedBy = "employee", cascade = [CascadeType.ALL])
    var wagePayments = mutableSetOf<EmployeeWagePayment>()

    @ManyToOne
    @JoinColumn(name="project")
    var project = Project()

}