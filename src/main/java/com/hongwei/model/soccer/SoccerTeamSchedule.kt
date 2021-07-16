package com.hongwei.model.soccer

data class SoccerTeamSchedule(
        val dataVersion: Long,
        val teamId: Long,
        val teamAbbr: String,
        val teamDisplayName: String,
        val league: String,
        val events: List<SoccerTeamEvent>
)

data class SoccerTeamEvent(
        val competitors: List<SoccerEventTeam>,
        val dateGMTString: String,
        val unixTimeStamp: Long?,
        val tbd: Boolean,
        val completed: Boolean,
        val league: String,
        val broadcasts: List<String> = emptyList(),
        val isHomeTeamWin: Boolean? = null,
        val scoreDisplay: String?,
        val venue: SoccerTeamVenue?
)

data class SoccerEventTeam(
        val teamId: Long,
        val abbrev: String,
        val displayName: String,
        val logo: String,
        val location: String,
        val isHome: Boolean,
        val score: Int,
        val winner: Boolean?
)

data class SoccerTeamVenue(
        val venue: String,
        val city: String?,
        val country: String?
)