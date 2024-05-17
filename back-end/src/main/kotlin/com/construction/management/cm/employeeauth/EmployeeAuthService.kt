package com.construction.management.cm.employeeauth

import com.construction.management.cm.config.JwtProperties
import com.construction.management.cm.dto.EmployeeLogIn
import com.construction.management.cm.dto.LoggedIn
import com.construction.management.cm.employee.EmployeeRepository
import com.construction.management.cm.exceptionhandler.CustomException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class EmployeeAuthService(
    private val employeeRepository: EmployeeRepository,
    private val repository: EmployeeAuthRepository,
    private val jwtProperties: JwtProperties
) {


    private val secretKey = Keys.hmacShaKeyFor(jwtProperties.key.toByteArray())

    @Autowired
    private lateinit var encoder: PasswordEncoder
    fun logIn(employeeLogIn: EmployeeLogIn): LoggedIn {
        when(logInValidations(employeeLogIn = employeeLogIn)) {
            "invalid-credentials" -> throw CustomException("invalid-credentials", null)
        }
        val employee = employeeRepository.getEmployeeEmailProjectUid(
            project = UUID.fromString(employeeLogIn.project),
            email = employeeLogIn.email.lowercase()
        )
        if (repository.findById(employee.id).isPresent) {
            repository.deleteById(employee.id)
        }
        val record = EmployeeAuth()
        val jwt = Jwts.builder()
            .claims()
            .subject(employee.id.toString())
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration))
            .and()
            .signWith(secretKey)
            .compact()
        record.id = employee.id
        record.token = jwt
        repository.save(record)
        return LoggedIn(
            token = jwt,
            firstName = employee.firstName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        )
    }

    fun logInValidations(employeeLogIn: EmployeeLogIn): String {
        /**
         * Check if employee exists,
         * Check if passwords match.
         */
        if (employeeRepository.employeeInProjectEmployeeIdProjectUid (
            project = UUID.fromString(employeeLogIn.project),
            email = employeeLogIn.email.lowercase()
        ) <= 0) {
            return "invalid-credentials"
        }
        val employee = employeeRepository.getEmployeeEmailProjectUid(
            project = UUID.fromString(employeeLogIn.project),
            email = employeeLogIn.email.lowercase()
        )
        if (!encoder.matches(employeeLogIn.password, employee.password)) {
            return "invalid-credentials"
        }
        return "ok"
    }


    fun validateRequestToken(token: String?): Long {
        if (token == null) {
            throw CustomException("auth-user-na", null)
        }
        val actualToken = token.substringAfter("Bearer ")
        val parser = Jwts.parser()
            .verifyWith(secretKey)
            .build()
        val claims: Claims? = parser.parseSignedClaims(actualToken).payload
        val userId: Long = claims?.subject?.toLong() ?: -1
        if (userId == -1L) {
            throw CustomException("auth-user-na", null)
        }
        if (!repository.findById(userId).isPresent) {
            throw CustomException("auth-user-na", null)
        }
        val record = repository.findById(userId).get()
        if (record.token != actualToken) {
            throw CustomException("auth-user-na", null)
        }
        if (claims!!.expiration.before(Date(System.currentTimeMillis()))) {
            repository.deleteById(userId)
            throw CustomException("auth-user-na", null)
        }
        return userId
    }

    fun logOut(user: Long, token: String): String {
        repository.deleteById(user)
        return "User logged out successfully"
    }


}