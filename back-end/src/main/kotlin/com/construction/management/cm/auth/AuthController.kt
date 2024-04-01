package com.construction.management.cm.auth

import com.construction.management.cm.dto.NewPassword
import com.construction.management.cm.dto.SignUp
import com.construction.management.cm.response.DefaultBoolean
import com.construction.management.cm.response.DefaultInt
import com.construction.management.cm.response.DefaultNa
import com.construction.management.cm.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("api/v1/auth")
class AuthController {

    @Autowired
    private lateinit var service: AuthService

    fun logIn() {
        TODO()
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

    @PostMapping("signup")
    fun signUp(@RequestBody signUpForm: SignUp): ResponseEntity<Any> {
        val createUser: Boolean = service.createUser(signUpForm)
        return ResponseEntity.status(200).body(
            DefaultNa(
                httpStatus = 200,
                message = "User Created Successfully"
            )
        )
    }

    @GetMapping("forgot-password")
    fun forgotPassword(@RequestParam(name = "email") email: String): ResponseEntity<Any> {
        val sentEmail: Boolean = service.forgotPasswordSendVerification(email = email)
        return ResponseEntity.status(200).body(DefaultBoolean(
            httpStatus = 200,
            message = "4 Digit Verification Code Sent to $email",
            bool = sentEmail
        ))
    }

    @PostMapping("new-password")
    fun changePassword(@RequestBody newPassword: NewPassword): ResponseEntity<Any> {
        val changePassword: Boolean = service.changePassword(newPassword)
        return ResponseEntity.status(200).body(DefaultNa(
            httpStatus = 200,
            message = "Password Changed Successfully"
        ))
    }



}