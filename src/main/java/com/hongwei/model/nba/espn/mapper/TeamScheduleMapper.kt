package com.hongwei.model.nba.espn.mapper

import com.hongwei.model.jpa.NbaTeamDetailEntity
import com.hongwei.model.jpa.NbaTeamScheduleEntity
import com.hongwei.model.nba.*
import com.hongwei.model.nba.espn.*
import com.hongwei.model.nba.espn.define.SeasonStage
import com.hongwei.model.nba.espn.define.SeasonTypeId
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date

object TeamScheduleMapper {
    fun map(team: String, teamScheduleSource: TeamScheduleSource): NbaTeamScheduleEntity = NbaTeamScheduleEntity(
            team = team.toLowerCase(),
            dataVersion = teamScheduleSource.dataVersion ?: 0,
            events = listOf(
                    teamScheduleSource.teamSchedule.first().events.post.map {
                        mapEventsSection(teamScheduleSource.teamSchedule.first().seasonType.type, it)
                    }.flatten(),
                    teamScheduleSource.teamSchedule.first().events.pre.map {
                        mapEventsSection(teamScheduleSource.teamSchedule.first().seasonType.type, it)
                    }.flatten()
            ).flatten()
    )

    fun teamEventMapToEvent(teamEvent: TeamEvent, teamDetailEntity: NbaTeamDetailEntity): Event = if (teamEvent.isHome) {
        Event(
                unixTimeStamp = teamEvent.unixTimeStamp,
                eventType = teamEvent.eventType,
                homeTeam = Team(
                        abbrev = teamDetailEntity.team,
                        displayName = teamDetailEntity.displayName,
                        logo = teamDetailEntity.logo,
                        location = teamDetailEntity.location
                ),
                guestTeam = Team(
                        abbrev = teamEvent.opponent.abbrev,
                        displayName = teamEvent.opponent.displayName,
                        logo = teamEvent.opponent.logo,
                        location = teamEvent.opponent.location
                ),
                result = teamEvent.result?.let { teamResult ->
                    Result(
                            homeTeamWinLossSymbol = teamResult.winLossSymbol,
                            homeTeamScore = teamResult.currentTeamScore,
                            guestTeamScore = teamResult.opponentTeamScore
                    )
                }
        )
    } else {
        Event(
                unixTimeStamp = teamEvent.unixTimeStamp,
                eventType = teamEvent.eventType,
                homeTeam = Team(
                        abbrev = teamEvent.opponent.abbrev,
                        displayName = teamEvent.opponent.displayName,
                        logo = teamEvent.opponent.logo,
                        location = teamEvent.opponent.location
                ),
                guestTeam = Team(
                        abbrev = teamDetailEntity.team,
                        displayName = teamDetailEntity.displayName,
                        logo = teamDetailEntity.logo,
                        location = teamDetailEntity.location
                ),
                result = teamEvent.result?.let { teamResult ->
                    Result(
                            homeTeamWinLossSymbol = if (teamResult.winLossSymbol == "W") "L" else "W",
                            homeTeamScore = teamResult.currentTeamScore,
                            guestTeamScore = teamResult.opponentTeamScore
                    )
                }
        )
    }

    private fun mapEventsSection(seasonType: Int, section: EventsSection): List<TeamEvent> =
            section.group.map {
                val date = parseDate(it.date.date)!!
                TeamEvent(
                        unixTimeStamp = date.time,
                        eventType = mapEventType(seasonType, section.title)?.name ?: "",
                        isHome = it.opponent.homeAwaySymbol != "@",
                        opponent = Team(
                                abbrev = EspnTeamMapper.mapLegacyTeamShort(it.opponent.abbrev),
                                displayName = it.opponent.displayName,
                                logo = it.opponent.logo,
                                location = it.opponent.location
                        ),
                        result = when (it.result.statusId.toIntOrNull()) {
                            ResultStatus.Finished.value -> TeamResult(
                                    winLossSymbol = it.result.winLossSymbol,
                                    currentTeamScore = it.result.currentTeamScore,
                                    opponentTeamScore = it.result.opponentTeamScore
                            )
                            else -> null
                        }
                )
            }

    private fun mapEventType(seasonType: Int, eventSectionTitle: String): EventType? = when (seasonType) {
        SeasonTypeId.PreSeason.id -> EventType.PreSeason
        SeasonTypeId.Regular.id -> EventType.Season
        SeasonTypeId.PlayInTournament.id -> EventType.PlayIn
        SeasonTypeId.PostSeason.id -> when (SeasonStage.parsePostSeasonTypeFromTitle(eventSectionTitle)) {
            SeasonStage.Round1 -> EventType.PlayOffRound1
            SeasonStage.Round2 -> EventType.PlayOffRound2
            SeasonStage.ConferenceFinal -> EventType.PlayOffConferenceFinal
            SeasonStage.GrandFinal -> EventType.PlayOffGrandFinal
            else -> null
        }
        else -> null
    }

    //2021-04-07T02:00Z
    private fun parseDate(dateString: String): Date? = try {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
        simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT")
        simpleDateFormat.parse(dateString)
    } catch (e: Exception) {
        null
    }
}