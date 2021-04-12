package com.hongwei.model.nba

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties
data class TeamScheduleSource(
        val dataVersion: Long?,
        val events: Events
)

@JsonIgnoreProperties
data class Events(
        val pre: List<Pre>
)

@JsonIgnoreProperties
data class Pre(
        val title: String,
        val group: List<EventSource>
)

@JsonIgnoreProperties
data class EventSource(
        val date: Date,
        val opponent: Opponent,
        val time: Time,
        val result: Result
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
data class Result(
        val isTie: Boolean,
        val winLossSymbol: String,
        val link: String,
        val statusId: String
)