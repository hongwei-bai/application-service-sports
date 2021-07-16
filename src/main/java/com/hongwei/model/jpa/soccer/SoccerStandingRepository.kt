package com.hongwei.model.jpa.soccer

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SoccerStandingRepository : JpaRepository<SoccerStandingEntity?, Long?> {
    @Query("from SoccerStandingEntity entity where entity.league=:league")
    fun findStandings(league: String): SoccerStandingEntity?
}