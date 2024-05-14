package com.construction.management.cm.taskhistory

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskHistoryRepository: JpaRepository<TaskHistory, Long> {
}