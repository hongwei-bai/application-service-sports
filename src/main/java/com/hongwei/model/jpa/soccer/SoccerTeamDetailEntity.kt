package com.hongwei.model.jpa.soccer

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id


@Entity
data class SoccerTeamDetailEntity(
        @Id @Column(nullable = false)
        var id: Int = 0,

        @Column(nullable = false)
        var league: String = "",

        @Column(nullable = false)
        var team: String = "",

        @Column(nullable = false)
        var displayName: String = "",

        @Column(nullable = false)
        var logo: String = "",

        @Column(nullable = false)
        var uid: String = "",

        @Column(nullable = false)
        var location: String = "",

        @Column(nullable = true)
        var teamColor: Long? = null
)

