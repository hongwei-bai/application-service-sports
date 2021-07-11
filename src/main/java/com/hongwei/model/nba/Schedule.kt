package com.hongwei.model.nba

data class Schedule(
        val dataVersion: Long = 0,
        val events: List<Event>
)

data class Event(
        val unixTimeStamp: Long,
        val eventType: String,
        val homeTeam: Team,
        val guestTeam: Team,
        val result: Result? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Event

        if (unixTimeStamp != other.unixTimeStamp) return false
        if (homeTeam != other.homeTeam) return false
        if (guestTeam != other.guestTeam) return false

        return true
    }

    override fun hashCode(): Int {
        var result = unixTimeStamp.hashCode()
        result = 31 * result + homeTeam.hashCode()
        result = 31 * result + guestTeam.hashCode()
        return result
    }
}

data class Result(
        val isHomeTeamWin: Boolean,
        val homeTeamScore: Int,
        val guestTeamScore: Int
)