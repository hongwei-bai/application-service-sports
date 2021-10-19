package com.hongwei.model.nba.theme_v1

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties
data class NbaTeamThemeJson(
        val data: List<NbaTeamThemeSource>
)

@JsonIgnoreProperties
data class NbaTeamThemeSource(
        val team: String,
        val teamColor: String?,
        val altColor: String?
)