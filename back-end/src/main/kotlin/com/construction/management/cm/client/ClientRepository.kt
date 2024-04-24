package com.construction.management.cm.client

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ClientRepository: JpaRepository<Client, Long> {


    @Query("select count(*) from client where project = :projectId", nativeQuery = true)
    fun numberOfClientsInProject(projectId: Long): Int

    @Query("select count(*) from client where project = :projectId and lower(name) = :name", nativeQuery = true)
    fun clientExists(projectId: Long, name: String): Int

}