package com.hongwei.model.nba

import com.hongwei.model.jpa.NbaTeamDetailEntity
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

    private fun convertColorHexStringToLong(colorHexString: String, argbString: String? = when (colorHexString.length) {
        6 -> "FF$colorHexString"
        8 -> colorHexString
        else -> null
    }): Long = argbString?.let {
        BigInteger(it, 16).toLong()
    } ?: 0L
}