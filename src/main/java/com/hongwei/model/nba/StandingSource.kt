package com.hongwei.model.nba

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties
data class StandingSource(
        val dataVersion: Long?,
        val easternConference: ConferenceStandingSource,
        val westernConference: ConferenceStandingSource
)

@JsonIgnoreProperties
data class ConferenceStandingSource(
        val title: String,
        val teams: List<TeamStandingSource>,
        val headers: List<String>,
        val headerAbbrs: List<String>
)

@JsonIgnoreProperties
data class TeamStandingSource(
        val rank: String,
        val displayName: String,
        val abbr: String,
        val abbrLegacy: String,
        val detail: MutableList<String>
)
