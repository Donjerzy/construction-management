package com.construction.management.cm.client

import org.springframework.stereotype.Service

@Service
class ClientService (private val repository: ClientRepository) {
    fun getNumberOfClientsInProject(projectId:Long):Int {
        return repository.numberOfClientsInProject(projectId)
    }


}