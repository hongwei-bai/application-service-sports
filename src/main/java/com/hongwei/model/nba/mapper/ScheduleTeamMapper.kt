package com.hongwei.model.nba.mapper

import com.hongwei.model.jpa.NbaTeamDetailEntity
import com.hongwei.model.nba.Team

object ScheduleTeamMapper {
    fun map(teamDetailEntity: NbaTeamDetailEntity): Team =
            Team(
                    abbrev = teamDetailEntity.team,
                    displayName = teamDetailEntity.displayName,
                    logo = teamDetailEntity.logo,
                    location = teamDetailEntity.location
            )
}