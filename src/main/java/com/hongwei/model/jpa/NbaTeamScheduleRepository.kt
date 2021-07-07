package com.hongwei.model.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface NbaTeamScheduleRepository : JpaRepository<NbaTeamScheduleEntity?, String?> {
    @Query("from NbaTeamScheduleEntity entity where entity.team=:team")
    fun findScheduleByTeam(@Param("team") team: String?): NbaTeamScheduleEntity?

    @Transactional
    @Modifying
    @Query("delete from NbaTeamScheduleEntity entity where entity.team=:team")
    fun deleteScheduleByTeam(@Param("team") team: String?)
}