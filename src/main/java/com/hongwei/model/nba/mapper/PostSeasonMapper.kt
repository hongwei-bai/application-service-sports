package com.hongwei.model.nba.mapper

import com.hongwei.model.jpa.nba.NbaPlayInEntity
import com.hongwei.model.jpa.nba.NbaPostSeasonEntity
import com.hongwei.model.nba.EventType
import com.hongwei.model.nba.PostSeason

object PostSeasonMapper {
    fun mapPlayIn(stage: EventType, playInEntity: NbaPlayInEntity?): PostSeason? =
            playInEntity?.let {
                PostSeason(
                        dataVersion = playInEntity.dataVersion,
                        currentStage = stage.name,
                        westernPlayInEventRound1 = playInEntity.westernPlayInEvents.subList(0, 2),
                        westernPlayInEventRound2 = playInEntity.westernPlayInEvents.last(),
                        easternPlayInEventRound1 = playInEntity.easternPlayInEvents.subList(0, 2),
                        easternPlayInEventRound2 = playInEntity.easternPlayInEvents.last()
                )
            }

    fun map(stage: EventType, nbaPostSeasonEntity: NbaPostSeasonEntity?): PostSeason? =
            nbaPostSeasonEntity?.let {
                PostSeason(
                        dataVersion = nbaPostSeasonEntity.dataVersion,
                        currentStage = stage.name,
                        westernPlayInEventRound1 = nbaPostSeasonEntity.westernPlayInEvents.subList(0, 2),
                        westernPlayInEventRound2 = nbaPostSeasonEntity.westernPlayInEvents.last(),
                        easternPlayInEventRound1 = nbaPostSeasonEntity.easternPlayInEvents.subList(0, 2),
                        easternPlayInEventRound2 = nbaPostSeasonEntity.easternPlayInEvents.last(),
                        westernRound1Series = nbaPostSeasonEntity.westernRound1Series,
                        easternRound1Series = nbaPostSeasonEntity.easternRound1Series,
                        westernRound2Series = nbaPostSeasonEntity.westernRound2Series,
                        easternRound2Series = nbaPostSeasonEntity.easternRound2Series,
                        westernConferenceFinal = nbaPostSeasonEntity.westernConferenceFinal,
                        easternConferenceFinal = nbaPostSeasonEntity.easternConferenceFinal,
                        final = nbaPostSeasonEntity.final
                )
            }
}