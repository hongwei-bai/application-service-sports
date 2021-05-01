package com.hongwei.model.nba.theme

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties
data class NbaTeamThemeJson(
        val dataVersion: Long?,
        val data: List<NbaTeamThemeSource>
)

@JsonIgnoreProperties
data class NbaTeamThemeSource(
        val team: String,
        val colorLight: String,
        val colorHome: String,
        val colorGuest: String
)