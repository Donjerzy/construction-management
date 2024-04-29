package com.construction.management.cm.wagetype

import com.construction.management.cm.employee.Employee
import com.construction.management.cm.user.User
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany

@Entity(name="wage_type")
class WageType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    val id: Long = 0

    @Column(name = "name", nullable = false, unique = true)
    var name: String = "_"

    @Column(name = "period_in_days", nullable = false, unique = true)
    var period: Int = 0

    //Table references

    @OneToMany(mappedBy = "wageType", targetEntity = Employee::class, cascade = [CascadeType.ALL])
    val employees = mutableSetOf<Employee>()

    @ManyToOne
    @JoinColumn(name = "project_manager")
    var projectManager = User()
}