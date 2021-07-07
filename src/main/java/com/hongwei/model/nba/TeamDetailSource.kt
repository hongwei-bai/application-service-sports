package com.hongwei.model.nba

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties
data class TeamDetailSource(
        val id: Int?,
        val abbrev: String?,
        val displayName: String?,
        val logo: String?,
        val teamColor: String?,
        val altColor: String?,
        val recordSummary: String?,
        val location: String?,
        val link: String
)