package com.construction.management.cm.employeewagepayment

import com.construction.management.cm.Runner.Runner
import com.construction.management.cm.dto.PaidPeriods
import com.construction.management.cm.employee.EmployeeRepository
import com.construction.management.cm.exceptionhandler.CustomException
import com.construction.management.cm.formatters.StringFormatter
import com.construction.management.cm.user.UserService
import org.springframework.stereotype.Service
import java.time.ZoneId
import java.util.*

@Service
class EmployeeWagePaymentService(
    private val repository: EmployeeWagePaymentRepository,
    private val userService: UserService,
    private val employeeRepository: EmployeeRepository,
    private val formatter: StringFormatter,
    private val runner: Runner
) {
    fun getPaidPeriods(userEmail: String, employeeId: Long): MutableSet<PaidPeriods> {
        when(getPaidPeriodsValidation(employeeId = employeeId, projectManager = userService.getUserId(userEmail)!!)) {
            "employee-doesn't-exist" -> throw CustomException("employee-doesn't-exist", null)
            "not-project-owner" -> throw CustomException("not-project-owner", null)
        }
        val fetchedPaidPeriods = repository.getByEmployeeOrderPeriodAsc(employeeId)
        val result = mutableSetOf<PaidPeriods>()
        for (period in fetchedPaidPeriods) {
            result.add(PaidPeriods(
                period = "${formatter.timestampToString(period.periodStart)}-${formatter.timestampToString(period.periodEnd)}"
            ))
        }
        return result
    }

    fun getPaidPeriodsValidation(employeeId: Long, projectManager: Long): String {
        if (!employeeRepository.findById(employeeId).isPresent) {
            return "employee-doesn't-exist"
        }
        val employee = employeeRepository.findById(employeeId).get()
        if (projectManager != employee.project.projectManager.id) {
            return "not-project-owner"
        }
        return "ok"
    }

    fun noPaymentsMade(employeeId: Long): Boolean {
        return repository.numberOfPaymentsMade(employeeId) == 0
    }

    fun nextPaymentDate(employeeId: Long): String {
        val lastPayment: Date = repository.lastPaymentDate(employeeId) ?: return formatter.timestampToString(employeeRepository.findById(employeeId).get().joinDate)
        val localDate = lastPayment.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val nextPaymentDate: Date = Date.from(localDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant())
        return formatter.timestampToString(nextPaymentDate)
    }

    fun lastPaymentMade(employeeId: Long): Date {
        return repository.lastPaymentDate(employeeId)!!
    }

}