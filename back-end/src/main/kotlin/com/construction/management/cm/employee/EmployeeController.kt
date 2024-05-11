package com.construction.management.cm.employee

import com.construction.management.cm.auth.TokenService
import com.construction.management.cm.dto.*
import com.construction.management.cm.response.DefaultNa
import com.construction.management.cm.response.GetContractResponse
import com.construction.management.cm.response.GetWageInfoResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


@RequestMapping("api/v1/employee")
@Controller
class EmployeeController(private val service: EmployeeService,
                        private val tokenService: TokenService) {

    @PostMapping("/add", consumes = ["multipart/form-data"])
    fun addEmployee(
                    @RequestHeader("Authorization") header:String,
                    @RequestParam(name = "firstName") firstName: String,
                    @RequestParam(name = "lastName") lastName: String,
                    @RequestParam(name = "email") email: String,
                    @RequestParam(name = "password") password: String,
                    @RequestParam(name = "wage") wage: Double,
                    @RequestParam(name = "joinDate") joinDate: String,
                    @RequestParam(name = "employeeType") employeeType: Long,
                    @RequestParam(name = "wageType") wageType: Long,
                    @RequestParam(name = "project") project: Long,
                    @RequestParam(name = "contract") contract: MultipartFile? = null
    ): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val employee = AddEmployee(
            firstName = firstName,
            contract = contract,
            email = email,
            employeeType = employeeType,
            wage = wage,
            wageType = wageType,
            joinDate = joinDate,
            lastName = lastName,
            password = password,
            project = project
        )
        val message: String = service.addEmployee(employee = employee, userEmail = userEmail!!)
        return ResponseEntity.status(200).body(
            DefaultNa(
                httpStatus = 200,
                message = message
            )
        )
    }

    @PostMapping("/add-contract", consumes = ["multipart/form-data"])
    fun addContract(@RequestHeader("Authorization") header:String,
                    @RequestParam(name = "employeeId") employeeId: Long,
                    @RequestParam(name= "contract") contract: MultipartFile) : ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val message: String = service.addContract(employee= employeeId, userEmail = userEmail!!, contract = contract)
        return ResponseEntity.status(200).body(
            DefaultNa(
                httpStatus = 200,
                message = message
            )
        )

    }

    @GetMapping("/contract")
    fun getContract(@RequestHeader("Authorization") header:String,
                    @RequestParam(name = "employeeId") employeeId: Long,): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val contract: String = service.getContract(userEmail = userEmail!!, employeeId = employeeId)
        return ResponseEntity.status(200).body(
            GetContractResponse(
                httpStatus = 200,
                message = "Contract Retrieved Successfully",
                contract = contract
            )
        )
    }


    @PostMapping("/modify-name")
    fun modifyName(@RequestHeader("Authorization") header:String,
                   @RequestBody name: ModifyName
    ): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val message: String = service.modifyName(userEmail = userEmail, name = name)
        return ResponseEntity.status(200).body(
            DefaultNa(
                httpStatus = 200,
                message = message
            )
        )
    }

    @PostMapping("/modify-email")
    fun modifyEmail(@RequestHeader("Authorization") header:String,
                   @RequestBody email: ModifyEmail
    ): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val message: String = service.modifyEmail(userEmail = userEmail!!, email = email)
        return ResponseEntity.status(200).body(
            DefaultNa(
                httpStatus = 200,
                message = message
            )
        )
    }

    @PostMapping("/modify-password")
    fun modifyPassword(@RequestHeader("Authorization") header:String,
                       @RequestBody modifyBody: ModifyPassword
    ): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val message: String = service.modifyPassword(userEmail = userEmail!!, modifyBody = modifyBody)
        return ResponseEntity.status(200).body(
            DefaultNa(
                httpStatus = 200,
                message = message
            )
        )
    }

    @GetMapping("/wage-info")
    fun getWageInfo(
        @RequestHeader("Authorization") header:String,
        @RequestParam(name = "employee_id") employeeId: Long
    ): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val wageInfo = service.getWageInformation(userEmail = userEmail!!, employeeId = employeeId)
        return ResponseEntity.status(200).body(
            GetWageInfoResponse(
                httpStatus = 200,
                message = "Wage Information Retrieved Successfully",
                wageInfo = wageInfo
            )
        )
    }

}