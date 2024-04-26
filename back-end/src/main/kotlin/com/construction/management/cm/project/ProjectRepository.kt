package com.construction.management.cm.project

import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ProjectRepository: JpaRepository<Project, Long> {


    @Query("select count(*) from project where lower(name) = :project and project_manager = :user", nativeQuery = true)
    fun projectExists(project:String, user:Long): Int

    @Query("select count(*) from project where id = :project and project_manager = :user", nativeQuery = true)
    fun projectExistsId(project:Long, user:Long): Int

    @Query("select count(*) from project where project_id = :uuid", nativeQuery = true)
    fun projectUuidExists(uuid: UUID): Int

    @Query("select * from project where project_manager = :user", nativeQuery = true)
    fun getProjects(user: Long): MutableSet<Project>

    @Query("select count(*) from project where id = :project and project_manager = :projectManager", nativeQuery = true)
    fun isProjectManager(projectManager: Long, project:Long) : Int

    @Query("select total_budget_amount_spent from project where id = :project", nativeQuery = true)
    fun getProjectBudgetSpent(project: Long): Double

    @Query("select total_budget_amount_received - total_budget_amount_spent from project where id = :project", nativeQuery = true)
    fun getProjectBudgetAvailable(project:Long): Double
    @Transactional
    @Modifying
    @Query("update project set committed_budget = :amount where id = :projectId", nativeQuery = true)
    fun modifyCommittedAmount(projectId: Long, amount:Double)
    @Transactional
    @Modifying
    @Query("update project set total_budget_amount_received = :amount where id = :projectId", nativeQuery = true)
    fun modifyInvestedAmount(projectId: Long, amount: Double)

}