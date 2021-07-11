package com.hongwei.model.nba

data class TeamSchedule(
        val dataVersion: Long = 0,
        val team: Team,
        val events: List<TeamEvent>
)

data class TeamEvent(
        val unixTimeStamp: Long,
        val eventType: String,
        val isHome: Boolean,
        val opponent: Team,
        val result: TeamResult? = null
)

data class Team(
        val abbrev: String,
        val displayName: String,
        val logo: String,
        val location: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Team

        if (abbrev != other.abbrev) return false

        return true
    }

    override fun hashCode(): Int {
        return abbrev.hashCode()
    }
}

data class TeamResult(
        val isWin: Boolean,
        val currentTeamScore: Int,
        val opponentTeamScore: Int
)