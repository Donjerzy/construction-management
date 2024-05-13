package com.construction.management.cm.project

import com.construction.management.cm.auth.TokenService
import com.construction.management.cm.dto.*
import com.construction.management.cm.response.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.RequestEntity
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

    @GetMapping("/get-clients")
    fun getProjectClients(@RequestHeader("Authorization") header:String,
                          @RequestParam(name = "project") project:Long): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val clients = service.getClients(email = userEmail, project = project)
        return ResponseEntity.status(200).body(
            GetClientsResponse(
                httpStatus = 200,
                message = "Project Clients retrieved successfully",
                clients = clients
            )
        )
    }

    @PostMapping("/edit-client")
    fun editClient(@RequestHeader("Authorization") header:String,
                    @RequestBody client:EditClient): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val message = service.editClient(userEmail = userEmail!!, client = client)
        return ResponseEntity.status(200).body(
            DefaultNa(
                httpStatus = 200,
                message = message
            )
        )
    }


    @GetMapping("/get-employees")
    fun getProjectEmployees(@RequestHeader("Authorization") header:String,
                          @RequestParam(name = "project") project:Long): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val employees = service.getEmployees(email = userEmail!!, project = project)
        return ResponseEntity.status(200).body(
            GetEmployeesResponse(
                httpStatus = 200,
                message = "Project Employees retrieved successfully",
                employees = employees
            )
        )
    }

    @GetMapping("/get-employee")
    fun getEmployee(@RequestHeader("Authorization") header:String,
                    @RequestParam(name = "project") project:Long,
                    @RequestParam(name = "employee") employee:Long): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val employee = service.getProjectEmployee(email = userEmail!!, project = project, employee = employee)
        return ResponseEntity.status(200).body(
            GetEmployeeResponse(
                httpStatus = 200,
                message = "Employee Retrieved Successfully",
                employee = employee
            )
        )
    }

    @GetMapping("/code")
    fun getProjectCode(@RequestHeader("Authorization") header:String,
                       @RequestParam("project") project: Long): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val code = service.getProjectCode(userEmail = userEmail!!, project = project)
        return ResponseEntity.status(200).body(
            GetProjectCodeResponse(
                httpStatus = 200,
                message = "Project retrieved successfully",
                code = code
            )
        )
    }





}