package com.hongwei.model.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface TeamThemeRepository : JpaRepository<TeamThemeEntity?, String?> {
    @Query("from TeamThemeEntity entity where entity.league=:league and entity.team=:team")
    fun findTeamTheme(@Param("league") league: String, @Param("team") team: String): TeamThemeEntity?

    @Query("from TeamThemeEntity entity where entity.league=:league")
    fun findTeamsThemeByLeague(@Param("league") league: String): List<TeamThemeEntity>

    @Query("from TeamThemeEntity entity")
    fun findAllTeamThemes(): List<TeamThemeEntity>

    @Transactional
    @Modifying
    @Query("delete from TeamThemeEntity entity where entity.league=:league and entity.team=:team")
    fun deleteTeamTheme(@Param("league") league: String, @Param("team") team: String)
}