package com.construction.management.cm.wagetype

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface WageTypeRepository : JpaRepository<WageType, Long>  {


    @Query("select count(*) from wage_type", nativeQuery = true)
    fun isEmpty(): Int


}