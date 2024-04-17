package com.construction.management.cm.project

import com.construction.management.cm.auth.TokenService
import com.construction.management.cm.dto.AddProject
import com.construction.management.cm.response.DefaultNa
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.config.annotation.web.oauth2.resourceserver.OAuth2ResourceServerSecurityMarker
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
@Controller
@RequestMapping("api/v1/project")
class ProjectController {

    @Autowired
    lateinit var tokenService: TokenService

    @Autowired
    lateinit var service: ProjectService


    @PostMapping("/add")
    fun addProject(@RequestHeader("Authorization") header:String,
                   @RequestBody project: AddProject): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val message = service.addProject(name = project.name.lowercase(), userEmail = userEmail!!)
        return ResponseEntity.status(200).body(DefaultNa(
            httpStatus = 200,
            message = message
        ))
    }



}