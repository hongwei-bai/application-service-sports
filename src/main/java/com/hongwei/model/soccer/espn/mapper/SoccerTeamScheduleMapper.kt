package com.hongwei.model.soccer.espn.mapper

import com.hongwei.model.jpa.soccer.SoccerTeamScheduleEntity
import com.hongwei.model.soccer.*
import com.hongwei.model.soccer.espn.SoccerCompetitorSource
import com.hongwei.model.soccer.espn.SoccerTeamEventSource
import com.hongwei.model.soccer.espn.SoccerTeamScheduleSource
import com.hongwei.util.EspnDateTimeParseUtil.parseDate
import com.hongwei.util.TimeStampUtil
import java.util.regex.Pattern

object SoccerTeamScheduleMapper {
    fun mapToResponseBody(entity: SoccerTeamScheduleEntity): SoccerTeamSchedule =
            SoccerTeamSchedule(
                    dataVersion = entity.dataVersion,
                    teamId = entity.teamId,
                    teamAbbr = entity.teamAbbr,
                    teamDisplayName = entity.teamDisplayName,
                    league = entity.league,
                    events = listOf(entity.events, entity.finishedEvents).flatten().sortedByDescending { it.unixTimeStamp }
            )

    fun map(league: String, fixturesSource: SoccerTeamScheduleSource?, resultSourceList: List<SoccerTeamEventSource>? = null): SoccerTeamScheduleEntity? =
            fixturesSource?.let {
                SoccerTeamScheduleEntity(
                        teamId = fixturesSource.team.id,
                        dataVersion = TimeStampUtil.getTimeVersionWithMinute(),
                        teamAbbr = fixturesSource.team.abbrev,
                        teamDisplayName = fixturesSource.team.displayName,
                        league = league,
                        events = fixturesSource.events.map {
                            mapTeamEvent(it)
                        },
                        finishedEvents = resultSourceList?.map {
                            mapTeamEvent(it)
                        }?.sortedByDescending { it.unixTimeStamp }
                                ?: emptyList()
                )
            }

    private fun mapEventTeam(eventSource: SoccerCompetitorSource): SoccerEventTeam =
            SoccerEventTeam(
                    teamId = eventSource.id,
                    abbrev = eventSource.abbrev,
                    displayName = eventSource.displayName,
                    logo = eventSource.logo,
                    location = eventSource.location,
                    isHome = eventSource.isHome,
                    winner = eventSource.winner
            )

    private fun mapTeamEvent(eventSource: SoccerTeamEventSource): SoccerTeamEvent {
        var resultEnum: SoccerResultEnum? = null
        var winner: String? = null
        var ftScore: String? = null
        var penaltyScore: String? = null
        var aggregateScore: String? = null
        when {
            eventSource.notes.isEmpty() -> {
                if (eventSource.score?.isNotEmpty() == true) {
                    ftScore = eventSource.score.replace(" ", "")
                    val scoreArray = eventSource.score.split("-")
                    val homeScore = scoreArray.first().trim().toIntOrNull() ?: 0
                    val guestScore = scoreArray.last().trim().toIntOrNull() ?: 0
                    when {
                        homeScore == guestScore -> {
                            resultEnum = SoccerResultEnum.Tie
                        }
                        homeScore > guestScore -> {
                            resultEnum = SoccerResultEnum.FT
                            winner = eventSource.competitors.first { it.isHome }.abbrev
                        }
                        else -> {
                            resultEnum = SoccerResultEnum.FT
                            winner = eventSource.competitors.first { !it.isHome }.abbrev
                        }
                    }
                }
            }
            eventSource.notes.contains(FIRST_LEG) -> {
                resultEnum = SoccerResultEnum.FirstLeg
                if (eventSource.score?.isNotEmpty() == true) {
                    ftScore = eventSource.score.replace(" ", "")
                }
            }
            eventSource.notes.contains(ON_PENALTY_KICKS) -> {
                resultEnum = SoccerResultEnum.Penalty
                winner = getWinner(eventSource)
                penaltyScore = parseScore(eventSource.notes)
            }
            eventSource.notes.contains(WINS_ON_AWAY_GOALS) -> {
                resultEnum = SoccerResultEnum.AggregateAwayGoals
                winner = getWinner(eventSource)
                aggregateScore = parseScore(eventSource.notes)
            }
            eventSource.notes.contains(ON_AGGREGATE) -> {
                resultEnum = SoccerResultEnum.Aggregate
                winner = getWinner(eventSource)
                aggregateScore = parseScore(eventSource.notes)
            }
            else -> Unit
        }

        return SoccerTeamEvent(
                competitors = eventSource.competitors.map { mapEventTeam(it) },
                unixTimeStamp = parseDate(eventSource.date)?.time,
                completed = eventSource.completed,
                league = eventSource.league,
                broadcasts = eventSource.broadcasts?.map { it.name } ?: emptyList(),
                result = resultEnum,
                scoreDisplay = ftScore,
                penaltyScore = penaltyScore,
                aggregateScore = aggregateScore,
                winner = winner,
                venue = eventSource.venue?.let {
                    SoccerTeamVenue(
                            venue = it.fullName,
                            city = it.address?.city,
                            country = it.address?.country
                    )
                }
        )
    }

    private fun parseScore(notes: String): String? {
        val pattern = Pattern.compile(PATTERN_SCORE)
        val matcher = pattern.matcher(notes)
        if (matcher.find()) {
            return matcher.group(0)
        }
        return null
    }

    private fun getWinner(eventSource: SoccerTeamEventSource) = when {
        eventSource.notes.contains(eventSource.competitors.first().displayName) -> eventSource.competitors.first().abbrev
        eventSource.notes.contains(eventSource.competitors.last().displayName) -> eventSource.competitors.last().abbrev
        else -> null
    }

    private const val FIRST_LEG = "1st Leg"
    private const val ON_PENALTY_KICKS = "on Penalty Kicks"
    private const val WINS_ON_AWAY_GOALS = "wins on Away Goals"
    private const val ON_AGGREGATE = "on aggregate"
    private const val PATTERN_SCORE = "\\d+-\\d+"
}