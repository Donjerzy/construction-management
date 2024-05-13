package com.construction.management.cm.report

import com.construction.management.cm.dto.ProjectBudgetDb
import com.construction.management.cm.dto.ProjectBudgetResponse
import com.construction.management.cm.dto.ProjectReport
import com.construction.management.cm.formatters.StringFormatter
import com.construction.management.cm.project.Project
import com.construction.management.cm.project.ProjectRepository
import com.construction.management.cm.user.UserService
import org.springframework.stereotype.Service

@Service
class ReportsService (
    private val projectsRepository: ProjectRepository,
    private val userService: UserService,
    private val formatter: StringFormatter) {
    fun getDashboardReport(userEmail: String): ProjectReport {
        val userId: Long = userService.getUserId(userEmail)!!
        return ProjectReport(
            completeProjects = projectsRepository.getCompleteCount(userId),
            abandonedProjects = projectsRepository.getAbandonedCount(userId),
            ongoingProjects = projectsRepository.getOngoingCount(userId),
            totalAvailableBudget = formatter.doubleToString(projectsRepository.totalAvailableBudget(userId)),
            projectBudgets = projectBudgetDbToProjectBudgetResponse(projectsRepository.getProjects(userId))
        )
    }

    fun projectBudgetDbToProjectBudgetResponse(projectBudgetDb: MutableSet<Project>): MutableList<ProjectBudgetResponse> {
        val result = mutableListOf<ProjectBudgetResponse>()
        for (project in projectBudgetDb) {
            result.add(
                ProjectBudgetResponse(
                    name = project.name,
                    budget = formatter.doubleToString(project.totalBudgetAmountReceived)
                )
            )
        }
        return result
    }

}
