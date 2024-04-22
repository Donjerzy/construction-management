package com.construction.management.cm.client

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ClientRepository: JpaRepository<Client, Long> {


    @Query("select count(*) from client where project = :projectId", nativeQuery = true)
    fun numberOfClientsInProject(projectId: Long): Int

}