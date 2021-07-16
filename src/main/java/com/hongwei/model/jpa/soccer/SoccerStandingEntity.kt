package com.hongwei.model.jpa.soccer

import com.hongwei.model.jpa.converter.SoccerStandingsConverter
import com.hongwei.model.soccer.SoccerStandingStats
import javax.persistence.*

@Entity
data class SoccerStandingEntity(
        @Id @Column(nullable = false)
        var league: String = "",

        @Column(nullable = false)
        var dataVersion: Long = 0L,

        @Column(nullable = false)
        var leagueTitle: String = "",

        @Lob
        @Convert(converter = SoccerStandingsConverter::class)
        @Column(nullable = false)
        var standings: List<SoccerStandingStats> = emptyList()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SoccerStandingEntity

        if (league != other.league) return false
        if (leagueTitle != other.leagueTitle) return false
        if (standings != other.standings) return false

        return true
    }

    override fun hashCode(): Int {
        var result = league.hashCode()
        result = 31 * result + leagueTitle.hashCode()
        result = 31 * result + standings.hashCode()
        return result
    }
}

