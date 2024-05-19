package com.construction.management.cm.taskhistory

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TaskHistoryRepository: JpaRepository<TaskHistory, Long> {

    @Query("select * from task_history where task_id = :taskId order by entry_date;",nativeQuery = true)
    fun getTaskHistory(taskId: Long): MutableList<TaskHistory>

}