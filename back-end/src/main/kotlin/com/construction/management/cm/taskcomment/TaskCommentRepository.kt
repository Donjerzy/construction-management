package com.construction.management.cm.taskcomment

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TaskCommentRepository : JpaRepository<TaskComment, Long> {

    @Query("select * from task_comment where task = :task order by date", nativeQuery = true)
    fun getTaskComments(task: Long): MutableList<TaskComment>

    @Query("select * from task_comment where lower(comment_owner) = :employee", nativeQuery = true)
    fun getEmployeeComments(employee:String): MutableList<TaskComment>

}