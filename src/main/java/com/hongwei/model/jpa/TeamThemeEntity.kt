package com.hongwei.model.jpa

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass


@Entity
@IdClass(LeagueAndTeamId::class)
data class TeamThemeEntity(
        @Id
        @Column(nullable = false)
        var league: String = "",

        @Id
        @Column(nullable = false)
        var team: String = "",

        @Column(nullable = false)
        var dataVersion: Long = 0L,

        @Column(nullable = false)
        var colorLight: Long = 0L,

        @Column(nullable = false)
        var colorHome: Long = 0L,

        @Column(nullable = false)
        var colorGuest: Long = 0L
) : Serializable

class LeagueAndTeamId : Serializable {
    private val league: String = ""
    private val team: String = ""
}