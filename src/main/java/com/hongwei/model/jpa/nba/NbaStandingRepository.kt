package com.hongwei.model.jpa.nba

import com.hongwei.model.jpa.nba.NbaStandingEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface NbaStandingRepository : JpaRepository<NbaStandingEntity?, Long?> {
    @Query("from NbaStandingEntity entity order by entity.id desc")
    fun findLatestStandings(): List<NbaStandingEntity>?

    @Transactional
    @Modifying
    @Query("delete from NbaStandingEntity entity where entity.dataVersion=:dataVersion")
    fun deleteStandingByDataVersion(@Param("dataVersion") dataVersion: Long?)
}