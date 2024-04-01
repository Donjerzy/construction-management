package com.construction.management.cm.user

import com.construction.management.cm.dto.SignUp
import com.construction.management.cm.emailsender.EmailSender
import com.construction.management.cm.emailverification.EmailVerification
import com.construction.management.cm.emailverification.EmailVerificationRepository
import com.construction.management.cm.exceptionhandler.CustomException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import kotlin.random.Random

@Service
class UserService {

    @Autowired
    private lateinit var repository: UserRepository

    @Autowired
    private lateinit var emailSender: EmailSender

    @Autowired
    private lateinit var emailVerificationRepository: EmailVerificationRepository


    private val encoder = BCryptPasswordEncoder(16)

    fun userExists(email: String): Boolean {
        val userCount = repository.findUserCountByEmail(email=email.lowercase())
        return userCount > 0
    }


    fun generateFourDigitCode(): Int {
        val random = Random(System.currentTimeMillis())
        return (1000..9999).random(random)
    }


    fun signUpSendVerification(email: String): Boolean {
        when (userExists(email)) {
            true -> throw CustomException("user-exists-signup", null)
            else -> {
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
                user.firstName = signUpForm.firstName.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                user.surname = signUpForm.surname.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                user.password = encoder.encode(signUpForm.password)
                user.userRole = UserRole.BASE.name
                while (true) {
                    val identifier = UUID.randomUUID()
                    if (repository.findUserCountByUniqueIdentifier(identifier) <= 0) {
                        user.userIdentifier = identifier
                        break
                    }
                }
                try {
                    repository.save(user)
                } catch (e: Exception) {
                    throw CustomException("persist-sign-up-error", null)
                }
                return true
            }
        }
    }


}