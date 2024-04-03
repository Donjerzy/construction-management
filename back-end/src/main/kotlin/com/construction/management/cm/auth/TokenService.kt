package com.construction.management.cm.auth

import com.construction.management.cm.config.JwtProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date

@Service
class TokenService (jwtProperties: JwtProperties) {


    private val secretKey = Keys.hmacShaKeyFor(jwtProperties.key.toByteArray())

    fun generate(userDetails: UserDetails, expirationDate: Date, additionalClaims: Map<String, Any> = emptyMap()): String {
        return Jwts.builder()
                   .claims()
                   .subject(userDetails.username)
                   .issuedAt(Date(System.currentTimeMillis()))
                   .expiration(expirationDate)
                   .add(additionalClaims)
                   .and()
                   .signWith(secretKey)
                   .compact()
    }


    private fun getAllClaims(token: String): Claims {
        val parser = Jwts.parser()
            .verifyWith(secretKey)
            .build()
        return parser.parseSignedClaims(token).payload
    }

    fun extractEmail(token: String): String? {
       return getAllClaims(token).subject
    }


    fun isExpired(token: String): Boolean {
        return getAllClaims(token).expiration.before(Date(System.currentTimeMillis()))
    }

    fun isValid(token: String, userDetails: UserDetails): Boolean {
        val userId = extractEmail(token)
        return userDetails.username == userId && !isExpired(token)
    }

}