package com.hongwei.model.soccer

data class SoccerTeamSchedule(
        val dataVersion: Long,
        val teamId: Long,
        val teamAbbr: String,
        val teamDisplayName: String,
        val logo: String?,
        val location: String,
        val league: String,
        val events: List<SoccerTeamEvent>
)

data class SoccerTeamEvent(
        val opponent: SoccerTeam,
        val unixTimeStamp: Long?,
        val homeAway: SoccerHomeEnum,
        val completed: Boolean,
        val league: String,
        val broadcasts: List<String> = emptyList(),
        val result: SoccerResultEnum? = null,
        val score: String?,
        val otScore: String? = null,
        val penaltyScore: String? = null,
        val aggregateScore: String? = null,
        val winner: String? = null,
        val venue: SoccerTeamVenue?
)

data class SoccerTeam(
        val teamId: Long,
        val abbrev: String,
        val displayName: String,
        val logo: String?,
        val location: String
)

data class SoccerTeamVenue(
        val venue: String,
        val city: String?,
        val country: String?
)