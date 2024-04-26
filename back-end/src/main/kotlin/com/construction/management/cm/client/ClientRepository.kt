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

    @Query("select count(*) from client where lower(name) = :name and project = :projectId and id != :clientId", nativeQuery = true)
    fun clientExistsDifferentId(projectId: Long, name: String, clientId: Long): Int

    @Query("select * from client where project = :projectId and id = :id", nativeQuery = true)
    fun getClient(projectId: Long, id: Long): Client
    @Query("select count(*) from client where project = :projectId and id = :id", nativeQuery = true)
    fun clientExistsId(projectId: Long, id: Long): Int


}