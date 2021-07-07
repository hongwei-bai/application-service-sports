package com.hongwei.model.jpa

import com.hongwei.model.jpa.converter.NbaStandingsConverter
import com.hongwei.model.nba.TeamStandingData
import javax.persistence.*


@Entity
data class NbaStandingEntity(
        @Id @GeneratedValue
        private var id: Long? = null,

        @Column(nullable = false)
        var dataVersion: Long? = null,

        @Lob
        @Convert(converter = NbaStandingsConverter::class)
        @Column(nullable = true)
        var easternStandings: List<TeamStandingData>? = null,

        @Lob
        @Convert(converter = NbaStandingsConverter::class)
        @Column(nullable = true)
        var westernStandings: List<TeamStandingData>? = null
)

