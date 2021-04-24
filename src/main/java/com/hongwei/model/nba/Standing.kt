package com.hongwei.model.nba

data class Standing(
        var easternConference: ConferenceStanding,
        var westernConference: ConferenceStanding
)

data class ConferenceStanding(
        val title: String,
        val teams: List<TeamStanding>,
        val headers: List<String>,
        val headerAbbrs: List<String>
)

data class TeamStanding(
        val rank: String,
        val displayName: String,
        val abbr: String,
        val abbrLegacy: String,
        val detail: MutableList<String>
)
