package com.construction.management.cm.employeewagepayment

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface EmployeeWagePaymentRepository: JpaRepository<EmployeeWagePayment, Long> {

    @Query("select * from employee_wage_payment where employee = :employee order by period_start", nativeQuery = true)
    fun getByEmployeeOrderPeriodAsc(employee: Long): MutableList<EmployeeWagePayment>

    @Query("select count(*) from employee_wage_payment where employee = :employee", nativeQuery = true)
    fun numberOfPaymentsMade(employee: Long): Int
    @Query("select period_end from employee_wage_payment where employee = :employeeId order by period_end desc limit 1", nativeQuery = true)
    fun lastPaymentDate(employeeId: Long): Date?
    @Query("select coalesce(sum(coalesce(amount,0)),0) from employee_wage_payment where employee = :employeeId", nativeQuery = true)
    fun employeeWagesPaid(employeeId: Long): Double


}