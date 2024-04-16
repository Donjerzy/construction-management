package com.construction.management.cm.employeetype

import org.springframework.data.jpa.repository.JpaRepository

interface EmployeeTypeRepository: JpaRepository<EmployeeType, Long> {
}