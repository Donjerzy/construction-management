package com.construction.management.cm.task

import com.construction.management.cm.dto.ProjectTaskStatus
import org.springframework.stereotype.Service

@Service
class TaskService(private val repository: TaskRepository) {

    fun getProjectTaskStatus(project: Long): ProjectTaskStatus {
        return ProjectTaskStatus(
            done = repository.getProjectDoneTasksCount(project),
            ongoing = repository.getProjectPendingTasksCount(project)
        )
    }


}