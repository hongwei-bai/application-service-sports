package com.hongwei.model.nba

import com.hongwei.model.jpa.NbaTeamDetailEntity
import com.hongwei.model.jpa.NbaTeamScheduleEntity
import java.text.SimpleDateFormat
import java.time.Instant.now
import java.util.*
import java.util.Date

object TeamScheduleMapper {
    private const val WEEK_TS = 1000 * 3600 * 24 * 7L

    fun map(team: String, teamScheduleSource: TeamScheduleSource): NbaTeamScheduleEntity = NbaTeamScheduleEntity(
            team = team,
            dataVersion = teamScheduleSource.dataVersion ?: 0,
            events = listOf(
                    teamScheduleSource.teamSchedule.first().events.post.map { it.group }.flatten(),
                    teamScheduleSource.teamSchedule.first().events.pre.map { it.group }.flatten()
            ).flatten().filter {
                val lastWeekTs = Date.from(now()).time - WEEK_TS
                parseDate(it.date.date)?.after(Date(lastWeekTs)) == true
            }.map {
                val date = parseDate(it.date.date)!!
                TeamEvent(
                        unixTimeStamp = date.time,
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
    )

    fun teamEventMapToEvent(teamEvent: TeamEvent, teamDetailEntity: NbaTeamDetailEntity): Event = if (teamEvent.isHome) {
        Event(
                unixTimeStamp = teamEvent.unixTimeStamp,
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

    //2021-04-07T02:00Z
    private fun parseDate(dateString: String): Date? = try {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
        simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT")
        simpleDateFormat.parse(dateString)
    } catch (e: Exception) {
        null
    }
}