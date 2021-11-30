package com.hongwei.model.jpa.soccer

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SoccerTeamScheduleRepository : JpaRepository<SoccerTeamScheduleEntity?, Long?> {
    @Query("from SoccerTeamScheduleEntity entity where entity.teamId=:teamId")
    fun findTeamSchedule(teamId: Long): SoccerTeamScheduleEntity?

    @Query("from SoccerTeamScheduleEntity entity where entity.teamAbbr=:team")
    fun findTeamSchedule(team: String): SoccerTeamScheduleEntity?
}