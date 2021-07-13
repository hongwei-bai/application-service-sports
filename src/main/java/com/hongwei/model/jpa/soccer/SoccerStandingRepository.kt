package com.hongwei.model.jpa.soccer

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface SoccerStandingRepository : JpaRepository<SoccerStandingEntity?, Long?> {
    @Query("from SoccerStandingEntity entity  where entity.league=:league order by entity.dataVersion desc")
    fun findStandings(league: String): List<SoccerStandingEntity>?

    @Transactional
    @Modifying
    @Query("delete from SoccerStandingEntity entity where entity.dataVersion=:dataVersion")
    fun deleteStandingByDataVersion(@Param("dataVersion") dataVersion: Long?)
}