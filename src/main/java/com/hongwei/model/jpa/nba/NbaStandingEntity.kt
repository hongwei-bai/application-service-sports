package com.hongwei.model.jpa.nba

import com.hongwei.model.jpa.converter.NbaStandingsConverter
import com.hongwei.model.nba.TeamStanding
import javax.persistence.*


@Entity
data class NbaStandingEntity(
        @Id @GeneratedValue
        private var id: Long = 0L,

        @Column(nullable = false)
        var dataVersion: Long = 0L,

        @Lob
        @Convert(converter = NbaStandingsConverter::class)
        @Column(nullable = false)
        var eastern: List<TeamStanding> = emptyList(),

        @Lob
        @Convert(converter = NbaStandingsConverter::class)
        @Column(nullable = false)
        var western: List<TeamStanding> = emptyList()
)

