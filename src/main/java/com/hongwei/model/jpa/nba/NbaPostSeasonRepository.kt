package com.hongwei.model.jpa.nba

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

interface NbaPostSeasonRepository : JpaRepository<NbaPostSeasonEntity?, String?> {
    @Query("from NbaPostSeasonEntity entity order by entity.id desc")
    fun findPostSeason(): List<NbaPostSeasonEntity>

    @Transactional
    @Modifying
    @Query("delete from NbaPostSeasonEntity entity")
    fun deleteAllPostSeason()
}