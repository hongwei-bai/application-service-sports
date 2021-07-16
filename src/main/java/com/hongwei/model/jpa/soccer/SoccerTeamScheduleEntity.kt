package com.hongwei.model.jpa.soccer

import com.hongwei.model.jpa.converter.SoccerTeamScheduleConverter
import com.hongwei.model.soccer.SoccerTeamEvent
import javax.persistence.*

@Entity
data class SoccerTeamScheduleEntity(
        @Id @Column(nullable = false)
        var teamId: Long = 0L,

        @Column(nullable = false)
        var dataVersion: Long = 0L,

        @Column(nullable = false)
        var teamAbbr: String = "",

        @Column(nullable = false)
        var teamDisplayName: String = "",

        @Column(nullable = false)
        var league: String = "",

        @Lob
        @Convert(converter = SoccerTeamScheduleConverter::class)
        @Column(nullable = false)
        var events: List<SoccerTeamEvent> = emptyList(),

        @Lob
        @Convert(converter = SoccerTeamScheduleConverter::class)
        @Column(nullable = false)
        var finishedEvents: List<SoccerTeamEvent> = emptyList()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SoccerTeamScheduleEntity

        if (teamId != other.teamId) return false
        if (teamAbbr != other.teamAbbr) return false
        if (teamDisplayName != other.teamDisplayName) return false
        if (league != other.league) return false
        if (events != other.events) return false

        return true
    }

    override fun hashCode(): Int {
        var result = teamId.hashCode()
        result = 31 * result + teamAbbr.hashCode()
        result = 31 * result + teamDisplayName.hashCode()
        result = 31 * result + league.hashCode()
        result = 31 * result + events.hashCode()
        return result
    }
}