package com.construction.management.cm.Response

import com.construction.management.cm.User.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {

    @Query("select count(*) from user where lower(email)=:email", nativeQuery = true)
    fun findUserCountByEmail(email:String): Int


}