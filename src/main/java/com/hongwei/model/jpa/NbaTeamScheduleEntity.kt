package com.hongwei.model.jpa

import com.hongwei.model.jpa.converter.NbaScheduleEventListConverter
import com.hongwei.model.jpa.converter.NbaTeamDetailConverter
import com.hongwei.model.nba.Event
import com.hongwei.model.nba.TeamDetail
import javax.persistence.*


@Entity
data class NbaTeamScheduleEntity(
        @Id
        @Column(nullable = false)
        var team: String? = null,

        @Column(nullable = false)
        var dataVersion: Long? = null,

        @Lob
        @Convert(converter = NbaTeamDetailConverter::class)
        @Column(nullable = true)
        var teamDetail: TeamDetail? = null,

        @Lob
        @Convert(converter = NbaScheduleEventListConverter::class)
        @Column(nullable = true)
        var events: List<Event>? = null
)

