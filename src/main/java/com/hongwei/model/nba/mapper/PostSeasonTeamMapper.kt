package com.hongwei.model.nba.mapper

import com.hongwei.model.jpa.nba.NbaTeamDetailEntity
import com.hongwei.model.nba.PostSeasonTeam

object PostSeasonTeamMapper {
    fun map(teamDetailEntity: NbaTeamDetailEntity, rank: Int, seed: Int): PostSeasonTeam =
            PostSeasonTeam(
                    teamAbbr = teamDetailEntity.team,
                    displayName = teamDetailEntity.displayName,
                    logo = teamDetailEntity.logo,
                    location = teamDetailEntity.location,
                    rank = rank,
                    seed = seed,
                    recordSummaryWin = teamDetailEntity.recordSummaryWin,
                    recordSummaryLose = teamDetailEntity.recordSummaryLose,
                    isSurviveToPlayOff = true,
                    isSurvive = true
            )
}