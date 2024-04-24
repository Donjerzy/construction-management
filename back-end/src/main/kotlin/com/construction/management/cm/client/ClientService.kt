package com.construction.management.cm.client

import com.construction.management.cm.auth.TokenService
import com.construction.management.cm.dto.AddClient
import com.construction.management.cm.exceptionhandler.CustomException
import com.construction.management.cm.formatters.StringFormatter
import com.construction.management.cm.project.ProjectRepository
import com.construction.management.cm.project.ProjectService
import com.construction.management.cm.user.UserService
import jakarta.persistence.Column
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ClientService (private val repository: ClientRepository,
                     val stringFormatter: StringFormatter,
                     val projectRepository: ProjectRepository
                     ) {

    @Autowired
    private lateinit var userService: UserService


    fun getNumberOfClientsInProject(projectId:Long):Int {
        return repository.numberOfClientsInProject(projectId)
    }

    fun clientExists(projectId: Long, name:String):Boolean {
        return repository.clientExists(
            projectId = projectId,
            name = stringFormatter.formatNames(name).lowercase()) > 0
    }

    fun addClient(client: AddClient, userEmail: String): String {
        // Check if client is already in the project
        if (clientExists(projectId = client.project, name = client.name)) {
            throw CustomException("client-exists", null)
        }
        // Check if user is the project owner
        if(projectRepository.projectExistsId(
                project = client.project,
                user = userService.getUserId(email = userEmail)!!
            ) <= 0) {
           throw CustomException("project-doesn't-exists", null)
        }
        val newClient = Client()
        newClient.name = stringFormatter.formatNames(client.name).uppercase()
        newClient.type = when(client.type.lowercase()) {
            "individual" -> "INDIVIDUAL"
            "group" -> "GROUP"
            "organisation" -> "ORGANISATION"
            else -> throw CustomException("invalid-client-type",null)
        }
        newClient.committedAmount = client.committedAmount
        newClient.investedAmount = client.investedAmount
        newClient.project = projectRepository.findById(client.project).get()
        repository.save(newClient)
        return "Client Added Successfully"
    }
}