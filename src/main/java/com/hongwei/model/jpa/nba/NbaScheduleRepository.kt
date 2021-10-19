package com.hongwei.model.jpa.nba

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

interface NbaScheduleRepository : JpaRepository<NbaScheduleEntity?, String?> {
    @Query("from NbaScheduleEntity entity order by entity.id desc")
    fun findSchedule(): List<NbaScheduleEntity>

    @Transactional
    @Modifying
    @Query("delete from NbaScheduleEntity entity")
    fun deleteAllSchedules()
}