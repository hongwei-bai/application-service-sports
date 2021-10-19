package com.hongwei.model.nba.mapper

import com.hongwei.constants.Constants
import com.hongwei.model.jpa.nba.NbaTeamDetailEntity
import com.hongwei.model.nba.TeamDetail
import java.util.*

object TeamDetailMapper {
    fun map(teamDetailEntity: NbaTeamDetailEntity): TeamDetail =
            TeamDetail(
                    team = teamDetailEntity.team,
                    displayName = teamDetailEntity.displayName,
                    logo = teamDetailEntity.logo,
                    banner = Constants.AppDataPath.TEAM_BANNER_URL.replace("{team}", teamDetailEntity.team.toLowerCase(Locale.US)),
                    background = Constants.AppDataPath.TEAM_BACKGROUND_URL.replace("{team}", teamDetailEntity.team.toLowerCase(Locale.US)),
                    teamColor = teamDetailEntity.teamColor,
                    altColor = teamDetailEntity.altColor
            )
}