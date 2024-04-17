package com.construction.management.cm.employee

import org.springframework.data.jpa.repository.JpaRepository

interface EmployeeRepository: JpaRepository<Employee, Long> {
}