package com.hongwei.model.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

interface NbaPostSeasonArchivedRepository : JpaRepository<NbaPostSeasonArchivedEntity?, String?> {
    @Query("from NbaPostSeasonArchivedEntity entity order by entity.id desc")
    fun findPostSeasonArchived(): List<NbaPostSeasonArchivedEntity>

    @Transactional
    @Modifying
    @Query("delete from NbaPostSeasonArchivedEntity entity")
    fun deleteAllPostSeasonArchived()
}