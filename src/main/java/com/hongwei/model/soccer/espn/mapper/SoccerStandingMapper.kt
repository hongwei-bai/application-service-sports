package com.hongwei.model.soccer.espn.mapper

import com.hongwei.model.jpa.soccer.SoccerStandingEntity
import com.hongwei.model.soccer.espn.SoccerStandingSourceOutput
import com.hongwei.model.soccer.espn.SoccerStandingTeamWrapSource
import com.hongwei.model.soccer.SoccerStanding
import com.hongwei.model.soccer.SoccerStandingStats

object SoccerStandingMapper {
    fun mapToResponseBody(entity: SoccerStandingEntity): SoccerStanding =
            SoccerStanding(
                    dataVersion = entity.dataVersion,
                    league = entity.league,
                    leagueTitle = entity.leagueTitle,
                    standings = entity.standings
            )

    fun map(league: String, source: SoccerStandingSourceOutput): SoccerStandingEntity =
            SoccerStandingEntity(
                    league = league,
                    dataVersion = source.dataVersion,
                    leagueTitle = source.data.page.content.standings.groups.groups.firstOrNull()?.name ?: "",
                    standings = source.data.page.content.standings.groups.groups.firstOrNull()?.standings
                            ?.map { mapStandingTeam(it) } ?: emptyList()
            )

    private fun mapStandingTeam(source: SoccerStandingTeamWrapSource): SoccerStandingStats =
            SoccerStandingStats(
                    teamId = source.team.id,
                    teamAbbr = source.team.abbrev.toLowerCase(),
                    displayName = source.team.displayName,
                    shortDisplayName = source.team.shortDisplayName,
                    logo = source.team.logo,
                    wins = source.stats[0].toInt(),
                    losses = source.stats[1].toInt(),
                    draws = source.stats[2].toInt(),
                    gamePlayed = source.stats[3].toInt(),
                    goalsFor = source.stats[4].toInt(),
                    goalsAgainst = source.stats[5].toInt(),
                    points = source.stats[6].toInt(),
                    rankChange = source.stats[7].toInt(),
                    rank = source.stats[8].toInt(),
                    goalDifference = source.stats[9].toInt(),
                    pointDeductions = source.stats[10],
                    pointsPerGame = source.stats[11].toInt()
            )
}