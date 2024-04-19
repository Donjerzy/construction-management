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
    lateinit var repository: UserRepository

    fun getUserId(email:String): Long? {
      return repository.getUserId(email.lowercase())
    }

    fun getUserFirstName(email: String): String {
        return repository.getUserFirstName(email.lowercase()).lowercase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    fun getUser(email: String): User? {
        return repository.findByEmail(email.lowercase())
    }






}