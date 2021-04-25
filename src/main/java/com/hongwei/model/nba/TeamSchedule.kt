package com.hongwei.model.nba

data class TeamSchedule(
        val dataVersion: Long = 0,
        val events: List<Event>
)

data class Event(
        val unixTimeStamp: Long,
        val localDisplayTime: String,
        val opponent: Team,
        val result: Result? = null
)

data class Team(
        val abbrev: String,
        val displayName: String,
        val logo: String,
        val location: String,
        val isHome: Boolean
)

data class Result(
        val winLossSymbol: String,
        val currentTeamScore: Int,
        val opponentTeamScore: Int
)