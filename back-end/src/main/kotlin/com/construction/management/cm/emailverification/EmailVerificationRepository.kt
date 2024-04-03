package com.construction.management.cm.emailverification

import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface EmailVerificationRepository: JpaRepository<EmailVerification, Long> {

    @Query("select count(*) from cm_email_verification where lower(email)=:email", nativeQuery = true)
    fun findUserCountByEmail(email:String): Int

    @Transactional
    @Modifying
    @Query("delete from cm_email_verification where lower(email)=:email", nativeQuery = true)
    fun deleteUserCode(email: String)

    @Query("select count(*) from cm_email_verification where lower(email)=:email and code=:code", nativeQuery = true)
    fun validateEmailCode(email: String, code:Int): Int

}