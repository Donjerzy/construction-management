package com.construction.management.cm.wagetype

import com.construction.management.cm.employee.Employee
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity(name="wage_type")
class WageType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    val id: Long = 0

    @Column(name = "name", nullable = false, unique = true)
    val name: String = "_"

    @Column(name = "period_in_days", nullable = false, unique = true)
    val period: Int = 0

    @OneToMany(mappedBy = "wageType", targetEntity = Employee::class, cascade = [CascadeType.ALL])
    val employees = mutableSetOf<Employee>()
}