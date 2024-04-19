package com.construction.management.cm.project

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface ProjectRepository: JpaRepository<Project, Long> {


    @Query("select count(*) from project where lower(name) = :project and project_manager = :user", nativeQuery = true)
    fun projectExists(project:String, user:Long): Int

    @Query("select count(*) from project where project_id = :uuid", nativeQuery = true)
    fun projectUuidExists(uuid: UUID): Int

    @Query("select * from project where project_manager = :user", nativeQuery = true)
    fun getProjects(user: Long): MutableSet<Project>


}