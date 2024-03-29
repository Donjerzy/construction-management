package com.construction.management.cm.User

import com.construction.management.cm.EmailSender.EmailSender
import com.construction.management.cm.EmailVerification.EmailVerification
import com.construction.management.cm.EmailVerification.EmailVerificationRepository
import com.construction.management.cm.ExceptionHandler.CustomException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class UserService {

    @Autowired
    private lateinit var repository: UserRepository

    @Autowired
    private lateinit var emailSender: EmailSender

    @Autowired
    private lateinit var emailVerificationRepository: EmailVerificationRepository

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
        when (emailVerificationRepository.validateEmailCode(email=email.lowercase(), code=code)) {
            0 -> throw CustomException("invalid-verification-code", null)
            else -> return code
        }

    }
}