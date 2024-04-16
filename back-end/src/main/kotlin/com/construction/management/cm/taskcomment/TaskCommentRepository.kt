package com.construction.management.cm.taskcomment

import org.springframework.data.jpa.repository.JpaRepository

interface TaskCommentRepository : JpaRepository<TaskComment, Long> {
}