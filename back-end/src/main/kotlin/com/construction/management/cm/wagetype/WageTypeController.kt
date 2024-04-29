package com.construction.management.cm.wagetype

import com.construction.management.cm.auth.TokenService
import com.construction.management.cm.response.WageTypeResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping


@Controller
@RequestMapping("api/v1/wage-type")
class WageTypeController(private val tokenService: TokenService,
                         private val service: WageTypeService) {

    @GetMapping("/all")
    fun getWageTypes(@RequestHeader("Authorization") header:String): ResponseEntity<Any> {
        val userEmail = tokenService.extractEmail(header.substringAfter("Bearer "))
        val wageTypes = service.getWageTypes(userEmail = userEmail!!)
        return ResponseEntity.status(200).body(
            WageTypeResponse(
                httpStatus = 200,
                message = "Wage Types Retrieved Successfully",
                wageTypes = wageTypes
            )
        )



    }


}