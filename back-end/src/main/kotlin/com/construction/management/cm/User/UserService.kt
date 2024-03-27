package com.construction.management.cm.User

import com.construction.management.cm.Response.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService {

    private lateinit var repository: UserRepository
    fun userExists(email: String): Boolean {
        val userCount = repository.findUserCountByEmail(email=email)
        return if (userCount>0) {
            true
        } else {
            false
        }
    }
}