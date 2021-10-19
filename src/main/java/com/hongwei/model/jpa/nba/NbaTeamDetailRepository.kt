package com.hongwei.model.jpa.nba

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface NbaTeamDetailRepository : JpaRepository<NbaTeamDetailEntity?, String?> {
    @Query("from NbaTeamDetailEntity entity where entity.team=:team")
    fun findTeamDetail(@Param("team") team: String?): NbaTeamDetailEntity?

    @Transactional
    @Modifying
    @Query("delete from NbaTeamDetailEntity entity where entity.team=:team")
    fun deleteTeamDetail(@Param("team") team: String?)
}