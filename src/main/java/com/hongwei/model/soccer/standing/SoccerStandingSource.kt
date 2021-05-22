package com.hongwei.model.soccer.standing

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

data class SoccerStandingSourceOutput(
        val dataVersion: String,
        val data: SoccerStandingSourceWrap
)

@JsonIgnoreProperties
data class SoccerStandingSourceWrap(
        val page: SoccerStandingSourceWrap_Page
)

@JsonIgnoreProperties
data class SoccerStandingSourceWrap_Page(
        val content: SoccerStandingSourceWrap_Content
)

@JsonIgnoreProperties
data class SoccerStandingSourceWrap_Content(
        val standings: SoccerStandingSourceWrap_Standing
)

@JsonIgnoreProperties
data class SoccerStandingSourceWrap_Standing(
        val groups: SoccerStandingSourceWrap_Groups
)

@JsonIgnoreProperties
data class SoccerStandingSourceWrap_Groups(
        val groups: List<SoccerStandingGroupsSource>
)

@JsonIgnoreProperties
data class SoccerStandingGroupsSource(
        val name: String,
        val abbreviation: String,
        val standings: List<SoccerStandingTeamWrapSource>
)

@JsonIgnoreProperties
data class SoccerStandingTeamWrapSource(
        val team: SoccerStandingTeamSource,
        val stats: List<String>,
        val note: SoccerStandingNoteSource
)

@JsonIgnoreProperties
data class SoccerStandingTeamSource(
        val id: Int,
        val abbrev: String,
        val displayName: String,
        val shortDisplayName: String,
        val logo: String,
        val uid: String,
        val recordSummary: String,
        val standingSummary: String,
        val location: String,
        val links: String
)

@JsonIgnoreProperties
data class SoccerStandingNoteSource(
        val description: String
)
