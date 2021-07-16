package com.hongwei.model.nba.espn.mapper

import com.hongwei.model.jpa.nba.NbaTeamDetailEntity
import com.hongwei.model.nba.espn.TeamDetailSource
import com.hongwei.util.TeamColorUtil.convertColorHexStringToLong
import java.math.BigInteger

object TeamDetailMapper {
    fun map(teamDetailSource: TeamDetailSource,
            recordSummaryBreakdown: List<String> = teamDetailSource.recordSummary.split("-")): NbaTeamDetailEntity = NbaTeamDetailEntity(
            team = teamDetailSource.abbrev.toLowerCase(),
            displayName = teamDetailSource.displayName,
            logo = teamDetailSource.logo,
            teamColor = convertColorHexStringToLong(teamDetailSource.teamColor),
            altColor = convertColorHexStringToLong(teamDetailSource.altColor),
            recordSummaryWin = recordSummaryBreakdown.first().toInt(),
            recordSummaryLose = recordSummaryBreakdown.last().toInt(),
            location = teamDetailSource.location
    )
}