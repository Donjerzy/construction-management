package com.construction.management.cm.client

import com.construction.management.cm.auth.TokenService
import com.construction.management.cm.dto.AddClient
import com.construction.management.cm.dto.ReceiveInvestment
import com.construction.management.cm.response.DefaultNa
import com.construction.management.cm.response.DefaultString
import com.construction.management.cm.response.GetExpectedInvestmentResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("api/v1/client")
class ClientController(private val tokenService: TokenService,
                       private val service: ClientService)    {

    @PostMapping("/add")
    fun addClient(@RequestHeader("Authorization") header:String,
                  @RequestBody client: AddClient): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val message: String = service.addClient(client = client, userEmail = userEmail!!)
        return ResponseEntity.status(200).body(
            DefaultNa(
                httpStatus = 200,
                message = message
            )
        )
    }

    @GetMapping("/expected-investment")
    fun getExpectedInvestment(@RequestHeader("Authorization") header:String,
                              @RequestParam("client") client: Long) : ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val expectedInvestment = service.getExpectedInvestment(userEmail = userEmail!!, client = client)
        return ResponseEntity.status(200).body(
            GetExpectedInvestmentResponse(
                httpStatus = 200,
                message = "Expected investment retrieved successfully",
                expectedInvestment = expectedInvestment
            )
        )
    }

    @PostMapping("/receive-investment")
    fun receiveInvestment(@RequestHeader("Authorization") header:String,
                          @RequestBody investment: ReceiveInvestment
                          ): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val message = service.receiveInvestment(userEmail= userEmail!!, investment = investment)
        return ResponseEntity.status(200).body(
            DefaultNa(
                httpStatus = 200,
                message = message
            )
        )
    }


}