package com.hongwei.model.soccer.espn.mapper

import com.hongwei.model.jpa.soccer.SoccerTeamScheduleEntity
import com.hongwei.model.soccer.SoccerEventTeam
import com.hongwei.model.soccer.SoccerTeamEvent
import com.hongwei.model.soccer.SoccerTeamVenue
import com.hongwei.model.soccer.espn.SoccerCompetitorSource
import com.hongwei.model.soccer.espn.SoccerTeamEventSource
import com.hongwei.model.soccer.espn.SoccerTeamScheduleSource
import com.hongwei.util.EspnDateTimeParseUtil.parseDate
import com.hongwei.util.TimeStampUtil

object SoccerTeamScheduleMapper {
    fun map(league: String, fixturesSource: SoccerTeamScheduleSource?, resultSourceList: List<SoccerTeamEventSource>? = null): SoccerTeamScheduleEntity? =
            fixturesSource?.let {
                SoccerTeamScheduleEntity(
                        teamId = fixturesSource.team.id,
                        dataVersion = TimeStampUtil.getTimeVersionWithMinute(),
                        teamAbbr = fixturesSource.team.abbrev,
                        teamDisplayName = fixturesSource.team.displayName,
                        league = league,
                        events = fixturesSource.events.map {
                            println("event: $it")
                            mapTeamEvent(it)
                        },
                        finishedEvents = resultSourceList?.map {
                            println("finished event: $it")
                            mapTeamEvent(it)
                        }?.sortedByDescending { it.unixTimeStamp }
                                ?: emptyList()
                )
            }

    private fun mapTeamEvent(eventSource: SoccerTeamEventSource): SoccerTeamEvent =
            SoccerTeamEvent(
                    competitors = eventSource.competitors.map { mapEventTeam(it) },
                    dateGMTString = eventSource.date,
                    unixTimeStamp = parseDate(eventSource.date)?.time,
                    tbd = eventSource.tbd,
                    completed = eventSource.completed,
                    league = eventSource.league,
                    broadcasts = eventSource.broadcasts?.map { it.name } ?: emptyList(),
                    isHomeTeamWin = eventSource.competitors.firstOrNull { it.isHome }?.winner == true,
                    scoreDisplay = eventSource.score,
                    venue = eventSource.venue?.let {
                        SoccerTeamVenue(
                                venue = it.fullName,
                                city = it.address?.city,
                                country = it.address?.country
                        )
                    }
            )

    private fun mapEventTeam(eventSource: SoccerCompetitorSource): SoccerEventTeam =
            SoccerEventTeam(
                    teamId = eventSource.id,
                    abbrev = eventSource.abbrev,
                    displayName = eventSource.displayName,
                    logo = eventSource.logo,
                    location = eventSource.location,
                    isHome = eventSource.isHome,
                    score = eventSource.score,
                    winner = eventSource.winner
            )
}