package com.construction.management.cm.User

import com.construction.management.cm.Response.DefaultBoolean
import com.construction.management.cm.Response.DefaultInt
import com.construction.management.cm.Response.UserExists
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("api/v1/user/")
class UserController {

    @Autowired
    private lateinit var service:UserService

    fun logIn() {
        TODO()
    }

    @GetMapping("exists")
    fun validateNewUser(@RequestParam(name = "email") email: String): ResponseEntity<Any> {
        val userExists = service.userExists(email=email)
        return ResponseEntity.status(200).body(DefaultBoolean(
            httpStatus = 200,
            message = "User exist check completed successfully",
            bool = userExists
        )
        )
    }

    @GetMapping("sign-up/send-verification")
    fun signUpSendVerification(@RequestParam(name = "email") email: String): ResponseEntity<Any> {
        val isEmailSent = service.signUpSendVerification(email=email)
        return ResponseEntity.status(200).body(
            DefaultBoolean(
                httpStatus = 200,
                message = "4 Digit Verification Code Sent to $email",
                bool = isEmailSent
            )
        )
    }

    @GetMapping("validate-code")
    fun validateUserCode(@RequestParam(name = "email") email:String,
                         @RequestParam(name = "code") code:Int): ResponseEntity<Any> {
        val validCode = service.validateUserCode(email=email, code=code)
        return ResponseEntity.status(200).body(
            DefaultInt(
                httpStatus = 200,
                message = "Valid Code Entered",
                value = validCode
            )
        )
    }


    fun signUp() {
       TODO()
    }


    fun forgotPassword() {
        TODO()
    }



}