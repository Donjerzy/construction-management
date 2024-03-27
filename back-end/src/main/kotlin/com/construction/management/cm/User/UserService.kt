package com.construction.management.cm.User

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService {

    @Autowired
    private lateinit var repository: UserRepository
    fun userExists(email: String): Boolean {
        val userCount = repository.findUserCountByEmail(email=email)
        return userCount > 0
    }
}