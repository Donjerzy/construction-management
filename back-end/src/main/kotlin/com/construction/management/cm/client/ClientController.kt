package com.construction.management.cm.client

import com.construction.management.cm.auth.TokenService
import com.construction.management.cm.dto.AddClient
import com.construction.management.cm.response.DefaultNa
import com.construction.management.cm.response.DefaultString
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping

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

}