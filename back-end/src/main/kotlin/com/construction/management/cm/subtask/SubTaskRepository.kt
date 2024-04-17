package com.construction.management.cm.subtask

import org.springframework.data.jpa.repository.JpaRepository

interface SubTaskRepository : JpaRepository<SubTask, Long> {
}