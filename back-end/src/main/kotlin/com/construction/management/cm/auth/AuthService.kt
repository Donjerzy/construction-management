package com.construction.management.cm.auth

import com.construction.management.cm.config.JwtProperties
import com.construction.management.cm.dto.AuthenticationRequest
import com.construction.management.cm.dto.NewPassword
import com.construction.management.cm.dto.SignUp
import com.construction.management.cm.emailsender.EmailSender
import com.construction.management.cm.emailverification.EmailVerification
import com.construction.management.cm.emailverification.EmailVerificationRepository
import com.construction.management.cm.exceptionhandler.CustomException
import com.construction.management.cm.user.User
import com.construction.management.cm.user.UserRepository
import com.construction.management.cm.user.UserRole
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import kotlin.random.Random

@Service
class AuthService {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var emailSender: EmailSender

    @Autowired
    private lateinit var emailVerificationRepository: EmailVerificationRepository

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var userDetailsService: CustomUserDetailsService

    @Autowired
    private lateinit var tokenService: TokenService

    @Autowired
    private lateinit var jwtProperties: JwtProperties


    fun userExists(email: String): Boolean {
        val userCount = userRepository.findUserCountByEmail(email=email.lowercase())
        return userCount > 0
    }

    @Autowired
    private lateinit var encoder: PasswordEncoder

    fun generateFourDigitCode(): Int {
        val random = Random(System.currentTimeMillis())
        return (1000..9999).random(random)
    }



    fun generateCodeSendEmailPersist(email: String) {
        val randomCode = generateFourDigitCode()
        try {
            emailSender.sendMailToUser(
                toEmail = email,
                body = "Your four digit code is $randomCode",
                subject = "Construction Management Email Verification"
            )
        } catch (e:Exception) {
            throw CustomException("cannot-send-email", additionalInfo = e.message)
        }
        val userCode = EmailVerification()
        if (emailVerificationRepository.findUserCountByEmail(email.lowercase()) > 0) {
            emailVerificationRepository.deleteUserCode(email=email.lowercase())
        }
        userCode.email = email.lowercase()
        userCode.code = randomCode
        emailVerificationRepository.save(userCode)
    }

    fun signUpSendVerification(email: String): Boolean {
        when (userExists(email)) {
            true -> throw CustomException("user-exists-signup", null)
            else -> {
                generateCodeSendEmailPersist(email = email)
                return true
            }
        }
    }


    fun validateUserCode(email: String, code: Int): Int {
        when(emailVerificationRepository.validateEmailCode(email=email.lowercase(), code=code)) {
            0 -> throw CustomException("invalid-verification-code", null)
            else -> return code
        }
    }


    fun createUser(signUpForm: SignUp): Boolean {
        when(emailVerificationRepository.validateEmailCode(email=signUpForm.email.lowercase(), code=signUpForm.verificationCode)) {
            0 -> throw CustomException("code-not-verified", null)
            else -> {
                emailVerificationRepository.deleteUserCode(email=signUpForm.email.lowercase())
                val user = User()
                user.email = signUpForm.email.lowercase()
                user.firstName = signUpForm.firstName.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()) else it.toString() }
                user.surname = signUpForm.surname.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()) else it.toString() }
                user.password = encoder.encode(signUpForm.password)
                user.userRole = UserRole.BASE.name
                while (true) {
                    val identifier = UUID.randomUUID()
                    if (userRepository.findUserCountByUniqueIdentifier(identifier) <= 0) {
                        user.userIdentifier = identifier
                        break
                    }
                }
                try {
                    userRepository.save(user)
                } catch (e: Exception) {
                    throw CustomException("persist-sign-up-error", null)
                }
                return true
            }
        }
    }

    fun forgotPasswordSendVerification(email: String): Boolean {
        when(userExists(email)) {
            false ->  throw CustomException("forgot-password-non-existent-user", null)
            else -> {
                generateCodeSendEmailPersist(email = email)
                return true
            }
        }
    }

    fun changePassword(newPassword: NewPassword): Boolean {
        when(emailVerificationRepository.validateEmailCode(email=newPassword.email.lowercase(), code=newPassword.code)) {
            0 -> throw CustomException("code-not-verified", null)
            else -> {
                emailVerificationRepository.deleteUserCode(email=newPassword.email.lowercase())
                userRepository.changePassword(
                    email = newPassword.email.lowercase(),
                    password = encoder.encode(newPassword.password)
                )
                return true
            }
        }
    }

    fun authenticateUser(authRequest: AuthenticationRequest): String {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                authRequest.email,
                authRequest.password
            )
        )
        val user = userDetailsService.loadUserByUsername(authRequest.email)
        return tokenService.generate(
            userDetails = user,
            expirationDate = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration)
        )
    }


}