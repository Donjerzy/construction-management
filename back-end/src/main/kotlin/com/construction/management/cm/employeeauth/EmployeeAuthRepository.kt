package com.construction.management.cm.employeeauth

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmployeeAuthRepository: JpaRepository<EmployeeAuth, Long> {
}