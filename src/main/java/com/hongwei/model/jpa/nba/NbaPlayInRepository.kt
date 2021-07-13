package com.hongwei.model.jpa.nba

import com.hongwei.model.jpa.nba.NbaPlayInEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

interface NbaPlayInRepository : JpaRepository<NbaPlayInEntity?, String?> {
    @Query("from NbaPlayInEntity entity order by entity.id desc")
    fun findPlayIn(): List<NbaPlayInEntity>

    @Transactional
    @Modifying
    @Query("delete from NbaPlayInEntity entity")
    fun deleteAllPlayIns()
}