package com.construction.management.cm.employee

import com.construction.management.cm.employeetype.EmployeeType
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

    @Column(name = "username", nullable = false, length = 50, unique = true)
    var username: String = "-"

    @Column(name = "password", nullable = false, length = 50)
    var password: String = "-"

    @Column(name = "wage")
    var wage: Double = 0.0

    @Column(name = "join_date", nullable = false)
    var joinDate: Date = Date()


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

}