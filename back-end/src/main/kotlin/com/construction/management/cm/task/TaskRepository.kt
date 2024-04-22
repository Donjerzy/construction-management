package com.construction.management.cm.task

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository: JpaRepository<Task, Long> {

    @Query("select count(*) from task where project = :project and lower(status) = 'ongoing'", nativeQuery = true)
    fun getProjectPendingTasksCount(project:Long): Int

    @Query("select count(*) from task where project = :project and lower(status) like '%done'", nativeQuery = true)
    fun getProjectDoneTasksCount(project:Long): Int


}