package com.hongwei.model.jpa.nba

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id


@Entity
data class NbaTeamDetailEntity(
        @Id
        @Column(nullable = false)
        var team: String = "",

        @Column(nullable = false)
        var displayName: String = "",

        @Column(nullable = false)
        var logo: String = "",

        @Column(nullable = false)
        var teamColor: Long = 0L,

        @Column(nullable = false)
        var altColor: Long = 0L,

        @Column(nullable = false)
        var recordSummaryWin: Int = 0,

        @Column(nullable = false)
        var recordSummaryLose: Int = 0,

        @Column(nullable = false)
        var location: String = ""
)

