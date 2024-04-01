package com.construction.management.cm.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, Long> {

    @Query("select count(*) from cm_user where lower(email)=:email", nativeQuery = true)
    fun findUserCountByEmail(email:String): Int

    @Query("select count(*) from cm_user where user_identifier=:uid", nativeQuery = true)
    fun findUserCountByUniqueIdentifier(uid:UUID): Int


}