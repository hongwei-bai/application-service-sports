package com.hongwei.model.nba

data class TeamSchedule(
        val dataVersion: Long = 0,
        val events: List<Event>
)

data class Event(
        val unixTimeStamp: Long,
        val localDisplayTime: String,
        val opponent: Team
)

data class Team(
        val abbrev: String,
        val displayName: String,
        val logo: String,
        val location: String,
        val isHome: Boolean
)