package com.hongwei.model.nba.espn.mapper

import com.hongwei.model.jpa.nba.NbaTeamDetailEntity
import com.hongwei.model.nba.Standing
import com.hongwei.model.nba.TeamStanding
import com.hongwei.model.nba.espn.StandingSource
import com.hongwei.model.nba.espn.TeamStandingSource

object StandingMapper {
    fun map(teamDetailMap: Map<String, NbaTeamDetailEntity>, standingSource: StandingSource): Standing = Standing(
            dataVersion = standingSource.dataVersion ?: 0,
            western = mutableListOf<TeamStanding>().apply {
                standingSource.westernConference.teams.forEachIndexed { i, teamStandingSource ->
                    add(mapTeamStandingData(i, teamDetailMap[teamStandingSource.abbr.toLowerCase()], teamStandingSource))
                }
            },
            eastern = mutableListOf<TeamStanding>().apply {
                standingSource.easternConference.teams.forEachIndexed { i, teamStandingSource ->
                    add(mapTeamStandingData(i, teamDetailMap[teamStandingSource.abbr.toLowerCase()], teamStandingSource))
                }
            }
    )

    private fun mapTeamStandingData(i: Int, team: NbaTeamDetailEntity?, teamStandingSource: TeamStandingSource): TeamStanding = TeamStanding(
            rank = teamStandingSource.rank.toIntOrNull() ?: (i + 1),
            teamAbbr = teamStandingSource.abbr.toLowerCase(),
            teamName = teamStandingSource.displayName,
            teamLogo = team?.logo ?: "",
            teamLocation = team?.location ?: "",
            wins = teamStandingSource.detail[0].toInt(),
            losses = teamStandingSource.detail[1].toInt(),
            pct = teamStandingSource.detail[2].toDouble(),
            gamesBack = teamStandingSource.detail[3].toDoubleOrNull() ?: 0.0,
            homeRecords = parseRecordString(teamStandingSource.detail[4]),
            awayRecords = parseRecordString(teamStandingSource.detail[5]),
            divisionRecords = parseRecordString(teamStandingSource.detail[6]),
            conferenceRecords = parseRecordString(teamStandingSource.detail[7]),
            pointsPerGame = teamStandingSource.detail[8].toDouble(),
            opponentPointsPerGame = teamStandingSource.detail[9].toDouble(),
            avePointsDiff = teamStandingSource.detail[10].toDouble(),
            currentStreak = parseStreakString(teamStandingSource.detail[11]),
            last10Records = parseRecordString(teamStandingSource.detail[12])
    )

    private fun parseStreakString(streakString: String): Pair<String, Int> {
        val winOrLose = streakString.substring(0, 1)
        val number = streakString.replace(winOrLose, "").toIntOrNull() ?: 0
        return Pair(winOrLose, number)
    }

    private fun parseRecordString(recordString: String): Pair<Int, Int> {
        val numbers = recordString.split("-")
        return Pair(numbers[0].toIntOrNull() ?: 0, numbers[1].toIntOrNull() ?: 0)
    }
}