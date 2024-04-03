package com.construction.management.cm.auth



import com.construction.management.cm.exceptionhandler.CustomException
import com.construction.management.cm.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service


typealias ApplicationUser = com.construction.management.cm.user.User

@Service
class CustomUserDetailsService: UserDetailsService {

    @Autowired
    private lateinit var userRepository: UserRepository
    override fun loadUserByUsername(username: String): UserDetails {
       return userRepository.findByEmail(username.lowercase())
            ?.mapToUserDetails() ?: throw CustomException("auth-user-na", null)
    }

    private fun ApplicationUser.mapToUserDetails(): UserDetails {
        return User.builder()
            .username(this.email)
            .password(this.password)
            .roles(this.userRole)
            .build()
    }

}