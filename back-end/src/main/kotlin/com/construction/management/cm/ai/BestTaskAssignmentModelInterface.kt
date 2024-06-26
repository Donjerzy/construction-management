package com.construction.management.cm.ai

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BestTaskAssignmentModelInterface: JpaRepository<BestTaskAssignmentModel, Long> {
}