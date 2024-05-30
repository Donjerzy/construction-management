package com.construction.management.cm.ai

import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TaskAssignmentRepository: JpaRepository<TaskAssignment, Long> {

    @Query("select count(*) from task_assignment where dataset = 'train'", nativeQuery = true)
    fun getTrainDataSetCount(): Int

    @Query("select count(*) from task_assignment", nativeQuery = true)
    fun getTotalDataSetCount(): Int

    @Transactional
    @Modifying
    @Query("delete from task_assignment where task_id = :taskId", nativeQuery = true)
    fun deleteTaskData(taskId: Long)


}