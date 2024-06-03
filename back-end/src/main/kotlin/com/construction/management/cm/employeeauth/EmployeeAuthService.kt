package com.construction.management.cm.employeeauth

import com.construction.management.cm.auth.TokenService
import com.construction.management.cm.config.JwtProperties
import com.construction.management.cm.dto.EmployeeLogIn
import com.construction.management.cm.dto.EmployeeLogIn2
import com.construction.management.cm.dto.LoggedIn
import com.construction.management.cm.dto.LoggedInEmployee
import com.construction.management.cm.employee.Employee
import com.construction.management.cm.employee.EmployeeRepository
import com.construction.management.cm.exceptionhandler.CustomException
import com.construction.management.cm.user.UserRepository
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
    private val jwtProperties: JwtProperties,
    private val userRepository: UserRepository

) {

    @Autowired
    lateinit var tokenService: TokenService


    private val secretKey = Keys.hmacShaKeyFor(jwtProperties.key.toByteArray())

    @Autowired
    private lateinit var encoder: PasswordEncoder
//    fun logIn(employeeLogIn: EmployeeLogIn): LoggedInEmployee {
//        when(logInValidations(employeeLogIn = employeeLogIn)) {
//            "invalid-credentials" -> throw CustomException("invalid-credentials", null)
//        }
//        val employee = employeeRepository.getEmployeeEmailProjectUid(
//            project = UUID.fromString(employeeLogIn.project),
//            email = employeeLogIn.email.lowercase()
//        )
//        if (repository.findById(employee.id).isPresent) {
//            repository.deleteById(employee.id)
//        }
//        val record = EmployeeAuth()
//        val jwt = Jwts.builder()
//            .claims()
//            .subject(employee.id.toString())
//            .issuedAt(Date(System.currentTimeMillis()))
//            .expiration(Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration))
//            .and()
//            .signWith(secretKey)
//            .compact()
//        record.id = employee.id
//        record.token = jwt
//        repository.save(record)
//        return LoggedInEmployee(
//            token = jwt,
//            firstName = employee.firstName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
//            project = employeeLogIn.project
//        )
//    }

    fun logInV2 (employeeLogIn: EmployeeLogIn2) : LoggedInEmployee {
        when (logInValidationsV2(
            employeeLogIn = employeeLogIn
        )) {
            "invalid-source" -> throw CustomException("invalid-source", null)
            "invalid-project" -> throw CustomException("invalid-project", null)
            "invalid-token" -> throw CustomException("invalid-token", null)
            "auth-user-na" -> throw CustomException("auth-user-na", null)
        }
        val employee: Employee = when (employeeLogIn.from.lowercase()) {
            "admin" -> {
                val userEmail: String = tokenService.extractEmail(token = employeeLogIn.token)!!
                employeeRepository.getEmployeeEmailProjectUid(
                    project = UUID.fromString(employeeLogIn.project),
                    email = userEmail.lowercase()
                )
            }
            else -> {
                val userId = validateRequestToken(employeeLogIn.token)
                employeeRepository.getEmployeeEmailProjectUid(
                    project = UUID.fromString(employeeLogIn.project),
                    email = employeeRepository.findById(userId).get().email.lowercase()
                )
            }
        }
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
        return LoggedInEmployee(
            token = jwt,
            firstName = employee.firstName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
            project = employeeLogIn.project
        )
    }

    fun logInValidationsV2(employeeLogIn: EmployeeLogIn2): String {
        val validSources = listOf("admin", "employee")
        if (employeeLogIn.from.lowercase() !in validSources) {
            return "invalid-source"
        }
        if (employeeLogIn.project.isEmpty() || employeeLogIn.project.isBlank()) {
            return "invalid-project"
        }
        if (employeeLogIn.token.isEmpty() || employeeLogIn.project.isBlank()) {
            return "invalid-token"
        }
        when (employeeLogIn.from.lowercase()) {
            "admin" -> {
                val userEmail: String = tokenService.extractEmail(token = employeeLogIn.token) ?: return "auth-user-na"
                if (
                    employeeRepository.employeeInProjectEmployeeIdProjectUid(
                        project = UUID.fromString(employeeLogIn.project),
                        email = userEmail.lowercase()
                    ) <= 0
                ) {
                    return "auth-user-na"
                }
            }
            "employee" -> {
                val userId = validateRequestToken(employeeLogIn.token)
                if (
                    employeeRepository.employeeInProjectEmployeeIdProjectUid(
                        project = UUID.fromString(employeeLogIn.project),
                        email = employeeRepository.findById(userId).get().email.lowercase()
                    ) <= 0
                ) {
                    return "auth-user-na"
                }
            }
        }
        return "ok"
    }

//    fun logInValidations(employeeLogIn: EmployeeLogIn): String {
//        /**
//         * Check if employee exists,
//         * Check if passwords match.
//         */
//        if (employeeRepository.employeeInProjectEmployeeIdProjectUid (
//            project = UUID.fromString(employeeLogIn.project),
//            email = employeeLogIn.email.lowercase()
//        ) <= 0) {
//            return "invalid-credentials"
//        }
//        val employee = employeeRepository.getEmployeeEmailProjectUid(
//            project = UUID.fromString(employeeLogIn.project),
//            email = employeeLogIn.email.lowercase()
//        )
//        if (!encoder.matches(employeeLogIn.password, employee.password)) {
//            return "invalid-credentials"
//        }
//        return "ok"
//    }


    fun validateRequestToken(token: String?): Long {
        if (token == null) {
            throw CustomException("auth-user-na", null)
        }
        val actualToken = token.substringAfter("CMT ")
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

    fun logOut(user: Long): String {
        val userEmail = employeeRepository.findById(user).get().email.lowercase()
        val fetchedUser = userRepository.findByEmail(userEmail)
        fetchedUser?.loggedIn = false
        if (fetchedUser != null) {
            userRepository.save(fetchedUser)
        }
        repository.deleteById(user)
        return "User logged out successfully"
    }


}