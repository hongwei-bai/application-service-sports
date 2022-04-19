package com.hongwei.model.nba.espn.mapper

import com.hongwei.model.jpa.nba.NbaTeamDetailEntity
import com.hongwei.model.jpa.nba.NbaTeamScheduleEntity
import com.hongwei.model.nba.*
import com.hongwei.model.nba.espn.EspnTeamMapper
import com.hongwei.model.nba.espn.EventsSection
import com.hongwei.model.nba.espn.ResultStatus
import com.hongwei.model.nba.espn.TeamScheduleSource
import com.hongwei.model.nba.espn.define.SeasonStage
import com.hongwei.model.nba.espn.define.SeasonTypeId
import com.hongwei.service.nba.NbaDetailService
import com.hongwei.util.EspnDateTimeParseUtil.parseDate
import com.hongwei.util.TimeStampUtil

object TeamScheduleMapper {
    fun map(team: String, teamScheduleSource: TeamScheduleSource, nbaDetailService: NbaDetailService): NbaTeamScheduleEntity = NbaTeamScheduleEntity(
        team = team.toLowerCase(),
        dataVersion = TimeStampUtil.getTimeVersionWithMinute(),
        events = listOf(
            teamScheduleSource.teamSchedule.first().events.post.map {
                mapEventsSection(teamScheduleSource.teamSchedule.first().seasonType.type, it, nbaDetailService)
            }.flatten(),
            teamScheduleSource.teamSchedule.first().events.pre.map {
                mapEventsSection(teamScheduleSource.teamSchedule.first().seasonType.type, it, nbaDetailService)
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
            eventStatus = teamEvent.eventStatus.name,
            result = teamEvent.result?.let { teamResult ->
                Result(
                    isHomeTeamWin = teamResult.isWin,
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
            eventStatus = teamEvent.eventStatus.name,
            result = teamEvent.result?.let { teamResult ->
                Result(
                    isHomeTeamWin = !teamResult.isWin,
                    homeTeamScore = teamResult.opponentTeamScore,
                    guestTeamScore = teamResult.currentTeamScore
                )
            }
        )
    }

    private fun mapEventsSection(seasonType: Int, section: EventsSection, nbaDetailService: NbaDetailService): List<TeamEvent> =
        section.group.map {
            val date = parseDate(it.date.date)!!
            TeamEvent(
                unixTimeStamp = date.time,
                eventType = mapEventType(seasonType, section.title)?.name ?: "",
                isHome = it.opponent.homeAwaySymbol != "@",
                opponent = Team(
                    abbrev = EspnTeamMapper.mapLegacyTeamShort(it.opponent.abbrev),
                    displayName = it.opponent.displayName,
                    logo = nbaDetailService.getAppLogoUrl(it.opponent.logo),
                    location = it.opponent.location
                ),
                eventStatus = when (it.result.statusId.toIntOrNull()) {
                    ResultStatus.Finished.value,
                    ResultStatus.NotStart.value -> EventStatus.Default
                    else -> EventStatus.Cancelled
                },
                result = when (it.result.statusId.toIntOrNull()) {
                    ResultStatus.Finished.value -> TeamResult(
                        isWin = it.result.winLossSymbol == "W",
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
}