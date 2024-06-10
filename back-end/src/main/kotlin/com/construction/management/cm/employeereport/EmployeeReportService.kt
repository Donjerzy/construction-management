package com.construction.management.cm.employeereport

import com.construction.management.cm.dto.EmployeeReportDto
import com.construction.management.cm.employee.EmployeeRepository
import com.construction.management.cm.formatters.StringFormatter
import org.springframework.stereotype.Service

@Service
class EmployeeReportService(
    private val employeeRepository: EmployeeRepository,
    private val formatter: StringFormatter
) {
    fun getDashboardReport(userId: Long): EmployeeReportDto {
        return EmployeeReportDto (
            toDo = employeeRepository.getEmployeeToDoTasks(userId),
            done = employeeRepository.getEmployeeDoneTasks(userId),
            inProgress = employeeRepository.getEmployeeInProgressTasks(userId),
            averageTaskCompletionTime = when(employeeRepository.getEmployeeDoneTasks(userId)) {
                0 -> "n/a"
                else -> formatter.doubleToString(employeeRepository.getAverageTasksCompletionTime(userId))
            }
        )
    }
}