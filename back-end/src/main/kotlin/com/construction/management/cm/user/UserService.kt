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
class UserService