package com.hongwei.model.nba

import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.Instant.now
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.Date

object TeamScheduleMapper {
    private const val WEEK_TS = 1000 * 3600 * 24 * 7L

    fun map(teamScheduleSource: TeamScheduleSource): TeamSchedule = TeamSchedule(
            teamScheduleSource.dataVersion ?: 0,
            listOf(
                    teamScheduleSource.teamSchedule.first().events.post.firstOrNull()?.group ?: emptyList(),
                    teamScheduleSource.teamSchedule.first().events.pre.firstOrNull()?.group ?: emptyList()
            ).flatten().filter {
                val lastWeekTs = Date.from(now()).time - WEEK_TS
                parseDate(it.date.date)?.after(Date(lastWeekTs)) == true
            }.map {
                val date = parseDate(it.date.date)!!
                Event(
                        unixTimeStamp = date.time,
                        localDisplayTime = toLocalDisplayDateAndTime(date),
                        opponent = Team(
                                abbrev = EspnTeamMapper.mapLegacyTeamShort(it.opponent.abbrev),
                                displayName = it.opponent.displayName,
                                logo = it.opponent.logo,
                                location = it.opponent.location,
                                isHome = it.opponent.homeAwaySymbol != "@"
                        ),
                        result = when (it.result.statusId.toIntOrNull()) {
                            ResultStatus.Finished.value -> Result(
                                    winLossSymbol = it.result.winLossSymbol,
                                    currentTeamScore = it.result.currentTeamScore,
                                    opponentTeamScore = it.result.opponentTeamScore
                            )
                            else -> null
                        }
                )
            }
    )

    //2021-04-07T02:00Z
    private fun parseDate(dateString: String): Date? = try {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
        simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT")
        simpleDateFormat.parse(dateString)
    } catch (e: Exception) {
        null
    }

    private fun toLocalDisplayDateAndTime(date: Date): String {
        val cal = Calendar.getInstance()
        cal.time = date

        val instant = Instant.ofEpochMilli(cal.timeInMillis)
        val localDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate()
        val localTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalTime()
        return "$localDate $localTime"
    }
}