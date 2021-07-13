package com.hongwei.model.soccer.espn.mapper

import com.hongwei.model.jpa.soccer.SoccerTeamDetailEntity
import com.hongwei.model.soccer.espn.SoccerStandingSourceOutput

object SoccerDetailMapper {
    fun map(league: String, sourceStandingSourceOutput: SoccerStandingSourceOutput): List<SoccerTeamDetailEntity>? =
            sourceStandingSourceOutput.data.page.content.standings.groups.groups.firstOrNull()?.standings?.map { source ->
                SoccerTeamDetailEntity(
                        id = source.team.id,
                        league = league,
                        team = source.team.abbrev.toLowerCase(),
                        displayName = source.team.displayName,
                        logo = source.team.logo,
                        uid = source.team.uid,
                        location = source.team.location
                )
            }
}