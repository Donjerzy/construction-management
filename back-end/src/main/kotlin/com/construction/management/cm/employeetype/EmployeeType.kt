package com.construction.management.cm.employeetype

import com.construction.management.cm.employee.Employee
import com.construction.management.cm.user.User
import jakarta.persistence.*

@Entity(name="employee_type")
class EmployeeType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    val id: Long = 0

    @Column(name = "name", unique = true, nullable = false)
    var name: String = "-"


    // Table references
    @OneToMany(mappedBy = "employeeType", targetEntity = Employee::class, cascade = [CascadeType.ALL])
    val employees = mutableSetOf<Employee>()

    @ManyToOne
    @JoinColumn(name = "project_manager")
    val projectManager = User()


}