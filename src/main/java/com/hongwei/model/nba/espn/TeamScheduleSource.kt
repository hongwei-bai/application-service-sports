package com.hongwei.model.nba.espn

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties
data class TeamScheduleSource(
        val dataVersion: Long?,
        val teamSchedule: List<TeamScheduleElement>
)

@JsonIgnoreProperties
data class TeamScheduleElement(
        val season: Int,
        val seasonType: SeasonType,
        val title: String,
        val notes: String,
        val events: Events
)

@JsonIgnoreProperties
data class SeasonType(
        val id: String,
        val type: Int,
        val name: String,
        val abbreviation: String
)

@JsonIgnoreProperties
data class Events(
        val pre: List<EventsSection>,
        val post: List<EventsSection>
)

@JsonIgnoreProperties
data class EventsSection(
        val title: String,
        val group: List<EventSource>
)

@JsonIgnoreProperties
data class EventSource(
        val date: Date,
        val opponent: Opponent,
        val time: Time,
        val result: ResultSource
)

@JsonIgnoreProperties
data class Date(
        val date: String,
        val format: String,
        val formatMobile: String,
        val isTimeTBD: Boolean
)

@JsonIgnoreProperties
data class Opponent(
        val id: Int,
        val abbrev: String,
        val displayName: String,
        val logo: String,
        val recordSummary: String,
        val standingSummary: String,
        val location: String,
        val links: String,
        val homeAwaySymbol: String,
        val rank: String,
        val neutralSite: Boolean
)

@JsonIgnoreProperties
data class Time(
        val time: String,
        val link: String,
        val state: String,
        val tbd: Boolean,
        val format: String
)

@JsonIgnoreProperties
data class ResultSource(
        val winner: Boolean,
        val isTie: Boolean,
        val winLossSymbol: String,
        val currentTeamScore: Int,
        val opponentTeamScore: Int,
        val link: String,
        val statusId: String
)

enum class ResultStatus(val value: Int) {
    NotStart(1), Finished(3)
}