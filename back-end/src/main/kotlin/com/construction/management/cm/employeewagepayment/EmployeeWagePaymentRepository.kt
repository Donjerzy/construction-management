package com.construction.management.cm.employeewagepayment

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface EmployeeWagePaymentRepository: JpaRepository<EmployeeWagePayment, Long> {

    @Query("select * from employee_wage_payment where employee = :employee order by period_start", nativeQuery = true)
    fun getByEmployeeOrderPeriodAsc(employee: Long): MutableList<EmployeeWagePayment>

}