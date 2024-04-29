package com.construction.management.cm.employeetype

import com.construction.management.cm.employee.Employee
import jakarta.persistence.*

@Entity(name="employee_type")
class EmployeeType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    val id: Long = 0

    @Column(name = "name", unique = true, nullable = false)
    var name: String = "-"

    @OneToMany(mappedBy = "employeeType", targetEntity = Employee::class, cascade = [CascadeType.ALL])
    val employees = mutableSetOf<Employee>()
}