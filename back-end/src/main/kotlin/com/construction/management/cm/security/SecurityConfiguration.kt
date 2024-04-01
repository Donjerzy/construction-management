package com.construction.management.cm.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.DefaultSecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfiguration {


    @Bean
    fun securityFilterChain(http: HttpSecurity) : DefaultSecurityFilterChain {
        return http
            .csrf { it.disable() }
            .httpBasic {
                it
                    .disable()
            }
            .formLogin {
                it
                    .disable()
            }
            .build()
    }
}