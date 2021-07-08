package com.hongwei.model.jpa

import com.hongwei.model.jpa.converter.NbaScheduleTeamEventListConverter
import com.hongwei.model.nba.TeamEvent
import javax.persistence.*


@Entity
data class NbaTeamScheduleEntity(
        @Id
        @Column(nullable = false)
        var team: String = "",

        @Column(nullable = false)
        var dataVersion: Long = 0L,

        @Lob
        @Convert(converter = NbaScheduleTeamEventListConverter::class)
        @Column(nullable = false)
        var events: List<TeamEvent> = emptyList()
)

