package com.hongwei.model.soccer.espn

data class SoccerTeamScheduleSource(
        val team: SoccerTeamSource,
        val events: List<SoccerTeamEventSource>,
        val dropdownLeagues: List<DropdownLeague>
)

data class SoccerTeamSource(
        val id: Long,
        val abbrev: String,
        val displayName: String,
        val shortDisplayName: String,
        val teamColor: String
)

data class SoccerTeamEventSource(
        val id: Long,
        val competitors: List<SoccerCompetitorSource>,
        val date: String,
        val tbd: Boolean,
        val completed: Boolean,
        val league: String,
        val broadcasts: List<SoccerBroadcastSource>?,
        val score: String?,
        val status: SoccerEventStatusSource?,
        val venue: SoccerVenueSource?
)

data class SoccerBroadcastSource(
        val name: String,
        val type: String,
        val market: String
)

data class SoccerEventStatusSource(
        val state: String,
        val detail: String,
        val link: String
)

data class SoccerVenueSource(
        val fullName: String,
        val address: SoccerVenueAddressSource?
)

data class SoccerVenueAddressSource(
        val city: String?,
        val country: String?
)

data class SoccerCompetitorSource(
        val id: Long,
        val abbrev: String,
        val displayName: String,
        val logo: String,
        val recordSummary: String,
        val standingSummary: String,
        val location: String,
        val links: String,
        val isHome: Boolean,
        val score: Int,
        val winner: Boolean? = false
)

data class DropdownLeague(
        val title: String,
        val value: String,
        val href: String
)