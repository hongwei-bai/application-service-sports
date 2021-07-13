package com.hongwei.model.jpa.soccer

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface SoccerTeamDetailRepository : JpaRepository<SoccerTeamDetailEntity?, String?> {
    @Query("from SoccerTeamDetailEntity entity where entity.team=:team")
    fun findTeamDetailByTeam(@Param("team") team: String): SoccerTeamDetailEntity?

    @Query("from SoccerTeamDetailEntity entity where entity.id=:id")
    fun findTeamDetailById(@Param("id") team: Int): SoccerTeamDetailEntity?

    @Transactional
    @Modifying
    @Query("delete from SoccerTeamDetailEntity entity where entity.team=:team")
    fun deleteTeamDetail(@Param("team") team: String?)
}