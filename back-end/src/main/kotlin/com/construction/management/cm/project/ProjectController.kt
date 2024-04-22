package com.construction.management.cm.project

import com.construction.management.cm.auth.TokenService
import com.construction.management.cm.dto.AddProject
import com.construction.management.cm.dto.Overview
import com.construction.management.cm.dto.Projects
import com.construction.management.cm.response.DefaultNa
import com.construction.management.cm.response.GetProjectsResponse
import com.construction.management.cm.response.OverviewResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

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
        return ResponseEntity.status(200).body(
            DefaultNa(
            httpStatus = 200,
            message = message
        )
        )
    }

    @GetMapping("/all")
    fun getProjects(@RequestHeader("Authorization") header:String): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val projects: MutableSet<Projects> = service.getProjects(userEmail = userEmail!!)
        return ResponseEntity.status(200).body(
            GetProjectsResponse(
                httpStatus = 200,
                message = "Projects retrieved successfully",
                setContainer = projects
            )
        )
    }

    @GetMapping("/overview")
    fun getOverview(@RequestHeader("Authorization") header:String, @RequestParam(name = "project") project:Long): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val overview = service.getOverview(email = userEmail, project = project)
        return ResponseEntity.status(200).body(
            OverviewResponse(
                httpStatus = 200,
                message = "Overview retrieved successfully",
                overview = overview
            )
        )
    }



}