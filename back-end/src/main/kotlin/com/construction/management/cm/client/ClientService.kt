package com.construction.management.cm.client

import com.construction.management.cm.auth.TokenService
import com.construction.management.cm.dto.AddClient
import com.construction.management.cm.dto.GetExpectedInvestment
import com.construction.management.cm.dto.ReceiveInvestment
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

    fun clientExistsId(projectId: Long, id:Long):Boolean {
        return repository.clientExistsId(
            projectId = projectId,
            id = id) > 0
    }


    fun clientExistsDifferentId(projectId: Long, name:String, clientId: Long):Boolean {
        return repository.clientExistsDifferentId(
            projectId = projectId,
            name = stringFormatter.formatNames(name).lowercase(),
            clientId = clientId) > 0
    }

    fun getClient(projectId: Long, id:Long): Client? {
        return repository.getClient(
            projectId = projectId,
            id = id
        )
    }

    fun saveClient(client: Client):Client {
        return repository.save(client)
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
        if (client.committedAmount < 0) {
            throw CustomException("committed-amount-lz", null)
        }
        if (client.investedAmount < 0) {
            throw CustomException("invested-amount-lz", null)
        }
        if (client.committedAmount < client.investedAmount) {
            throw CustomException("committed-invested-error", null)
        }
        val project = projectRepository.findById(client.project).get()
        project.committedBudgetAmount += client.committedAmount
        project.totalBudgetAmountReceived += client.investedAmount
        projectRepository.save(project)
        newClient.committedAmount = client.committedAmount
        newClient.investedAmount = client.investedAmount
        newClient.project = project
        repository.save(newClient)
        return "Client Added Successfully"
    }

    fun getExpectedInvestment(userEmail: String, client: Long): GetExpectedInvestment {
        when(getExpectedInvestmentValidations(
            projectManager = userService.getUserId(userEmail)!!,
            client = client
        )) {
            "client-doesn't-exist" -> throw CustomException("client-doesn't-exist", null)
            "not-project-owner" -> throw CustomException("not-project-owner", null)
        }
        val fetchedClient = repository.findById(client).get()
        val expectedInvestment = fetchedClient.committedAmount - fetchedClient.investedAmount
        return GetExpectedInvestment(
            client = fetchedClient.name,
            totalCommitted = stringFormatter.doubleToString(fetchedClient.committedAmount),
            totalInvested = stringFormatter.doubleToString(fetchedClient.investedAmount),
            remainingAmount = stringFormatter.doubleToString(expectedInvestment)
        )
    }

    fun getExpectedInvestmentValidations(
        projectManager: Long,
        client: Long
    ): String {
        if (!repository.findById(client).isPresent) {
            return "client-doesn't-exist"
        }
        val authenticProjectManager = repository.findById(client).get().project.projectManager.id
        if (authenticProjectManager != projectManager) {
            return "not-project-owner"
        }
        return "ok"
    }

    fun receiveInvestment(userEmail: String, investment: ReceiveInvestment): String {
        when(receiveInvestmentValidations(
            projectManager = userService.getUserId(userEmail)!!,
            investment = investment
        )) {
            "client-doesn't-exist" -> throw CustomException("client-doesn't-exist", null)
            "not-project-owner" -> throw CustomException("not-project-owner", null)
            "invalid-amount" -> throw CustomException("invalid-amount", null)
        }
        val fetchedClient = repository.findById(investment.client).get()
        val project = projectRepository.findById(fetchedClient.project.id).get()
        project.totalBudgetAmountReceived += investment.amount
        projectRepository.save(project)
        fetchedClient.investedAmount += investment.amount
        repository.save(fetchedClient)
        return "Investment recorded successfully"
    }

    fun receiveInvestmentValidations(projectManager: Long, investment: ReceiveInvestment): String {
        if (!repository.findById(investment.client).isPresent) {
            return "client-doesn't-exist"
        }
        val authenticProjectManager = repository.findById(investment.client).get().project.projectManager.id
        if (authenticProjectManager != projectManager) {
            return "not-project-owner"
        }
        if (investment.amount <= 0) {
            return "invalid-amount"
        }
        val fetchedClient = repository.findById(investment.client).get()
        val expectedInvestment = fetchedClient.committedAmount - fetchedClient.investedAmount
        if (investment.amount > expectedInvestment) {
            return "invalid-amount"
        }
        return "ok"
    }


}