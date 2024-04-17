package com.construction.management.cm.subtaskcomment

import org.springframework.data.jpa.repository.JpaRepository

interface SubTaskCommentRepository : JpaRepository<SubTaskComment, Long> {
}