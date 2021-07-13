package com.hongwei.service.nba

import com.google.gson.Gson
import com.hongwei.model.jpa.nba.*
import com.hongwei.model.nba.*
import com.hongwei.model.nba.espn.TeamDetailSource
import com.hongwei.model.nba.espn.TeamScheduleSource
import com.hongwei.model.nba.espn.mapper.TeamDetailMapper
import com.hongwei.model.nba.espn.mapper.TeamScheduleMapper
import com.hongwei.util.TimeStampUtil
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class NbaPlayInAnalysisService {
    private val logger: Logger = LogManager.getLogger(NbaPlayInAnalysisService::class.java)

    @Autowired
    private lateinit var nbaStandingRepository: NbaStandingRepository

    @Autowired
    private lateinit var nbaPlayInRepository: NbaPlayInRepository

    @Autowired
    private lateinit var nbaCurlService: EspnCurlService

    // Pre-requests: Standing in db
    @Throws(IOException::class)
    fun fetchPlayIn(ongoing: Boolean): NbaPlayInEntity? {
        val playInEntityDb: NbaPlayInEntity? =
                nbaPlayInRepository.findPlayIn().firstOrNull()
        if (!ongoing && playInEntityDb?.westernPlayInEvents?.isNotEmpty() == true && playInEntityDb.easternPlayInEvents.isNotEmpty()) {
            return playInEntityDb
        } else {
            nbaStandingRepository.findLatestStandings()?.firstOrNull()?.let { standing ->
                val westernSeeds = analysisConferencePlayIn(standing.western)
                val easternSeeds = analysisConferencePlayIn(standing.eastern)
                val entity = NbaPlayInEntity(
                        dataVersion = TimeStampUtil.getTimeVersionWithMinute(),
                        westernPlayInEvents = westernSeeds,
                        easternPlayInEvents = easternSeeds
                )
                if (playInEntityDb == entity) {
                    return playInEntityDb
                }
                nbaPlayInRepository.save(entity)
                return entity
            }
        }
        return null
    }

    private fun analysisConferencePlayIn(conferenceStandings: List<TeamStanding>): List<PlayInEvent?> {
        val seeds = Array(4) { "TBD" }
        val seedsBeforePlayIn = Array(4) { "TBD" }
        val playInTeamDetailMap = hashMapOf<String, NbaTeamDetailEntity>()

        var r1UpHomeWin = true
        var r1LowHomeWin = true
        var r2HomeWin = true

        val r1UpScore = Array(2) { 0 }
        val r1LowScore = Array(2) { 0 }
        val r2Score = Array(2) { 0 }

        var r2HomeTeam = "TBD"
        var r2GuestTeam = "TBD"

        conferenceStandings.filter { it.rank in 7..10 }.forEach { playInTeam ->
            val doc = nbaCurlService.getPlayedPlayInTeamScheduleCurlDoc(playInTeam.teamAbbr)
            playInTeamDetailMap[playInTeam.teamAbbr.toLowerCase()] = TeamDetailMapper.map(
                    Gson().fromJson(nbaCurlService.getTeamDetailJson(doc), TeamDetailSource::class.java)
            )
            val teamScheduleSourceObj = nbaCurlService.getTeamScheduleJson(doc)
            val teamScheduleEntity: NbaTeamScheduleEntity? = teamScheduleSourceObj?.let {
                TeamScheduleMapper.map(playInTeam.teamAbbr, Gson().fromJson(teamScheduleSourceObj, TeamScheduleSource::class.java))
            }
            teamScheduleEntity?.run {
                if (events.size == 1) {
                    when (events.first().result?.isWin) {
                        true -> {
                            seeds[0] = team.toLowerCase()
                            r1UpHomeWin = events.first().isHome
                            r2HomeTeam = events.first().opponent.abbrev.toLowerCase()
                            if (events.first().isHome) {
                                seedsBeforePlayIn[0] = team.toLowerCase()
                                seedsBeforePlayIn[1] = events.first().opponent.abbrev.toLowerCase()
                                r1UpScore[0] = events.first().result?.currentTeamScore ?: 0
                                r1UpScore[1] = events.first().result?.opponentTeamScore ?: 0
                            } else {
                                seedsBeforePlayIn[1] = team.toLowerCase()
                                seedsBeforePlayIn[0] = events.first().opponent.abbrev.toLowerCase()
                                r1UpScore[0] = events.first().result?.opponentTeamScore ?: 0
                                r1UpScore[1] = events.first().result?.currentTeamScore ?: 0
                            }
                        }
                        false -> {
                            seeds[3] = team.toLowerCase()
                            r1LowHomeWin = !events.first().isHome
                            r2GuestTeam = events.first().opponent.abbrev.toLowerCase()
                            if (events.first().isHome) {
                                seedsBeforePlayIn[2] = team.toLowerCase()
                                seedsBeforePlayIn[3] = events.first().opponent.abbrev.toLowerCase()
                                r1LowScore[0] = events.first().result?.currentTeamScore ?: 0
                                r1LowScore[1] = events.first().result?.opponentTeamScore ?: 0
                            } else {
                                seedsBeforePlayIn[3] = team.toLowerCase()
                                seedsBeforePlayIn[2] = events.first().opponent.abbrev.toLowerCase()
                                r1LowScore[0] = events.first().result?.currentTeamScore ?: 0
                                r1LowScore[1] = events.first().result?.opponentTeamScore ?: 0
                            }
                        }
                    }
                } else if (events.size == 2) {
                    when {
                        events.first().result?.isWin == true && events.last().result?.isWin == true -> {
                            seeds[1] = team.toLowerCase()
                            r2HomeWin = false
                            r2Score[0] = events.last().result?.opponentTeamScore ?: 0
                            r2Score[1] = events.last().result?.currentTeamScore ?: 0
                        }
                        events.first().result?.isWin == true && events.last().result?.isWin == false -> seeds[2] = team
                        events.first().result?.isWin == false && events.last().result?.isWin == true -> {
                            seeds[1] = team.toLowerCase()
                            r2HomeWin = true
                            r2Score[0] = events.last().result?.currentTeamScore ?: 0
                            r2Score[1] = events.last().result?.opponentTeamScore ?: 0
                        }
                        events.first().result?.isWin == false && events.last().result?.isWin == false -> seeds[2] = team
                    }
                }
            }
        }

        val playInEvents = listOf(
                PlayInEvent(
                        homeTeam = playInTeamDetailMap[seedsBeforePlayIn[0]]!!.run {
                            PostSeasonTeam(
                                    teamAbbr = team,
                                    displayName = displayName,
                                    logo = logo,
                                    location = location,
                                    rank = 7,
                                    seed = seeds.indexOf(team) + 7,
                                    recordSummaryWin = recordSummaryWin,
                                    recordSummaryLose = recordSummaryLose,
                                    isSurviveToPlayOff = true,
                                    isSurvive = true
                            )
                        },
                        guestTeam = playInTeamDetailMap[seedsBeforePlayIn[1]]!!.run {
                            PostSeasonTeam(
                                    teamAbbr = team,
                                    displayName = displayName,
                                    logo = logo,
                                    location = location,
                                    rank = 8,
                                    seed = seeds.indexOf(team) + 7,
                                    recordSummaryWin = recordSummaryWin,
                                    recordSummaryLose = recordSummaryLose,
                                    isSurviveToPlayOff = true,
                                    isSurvive = true
                            )
                        },
                        result = Result(
                                isHomeTeamWin = r1UpHomeWin,
                                homeTeamScore = r1UpScore.first(),
                                guestTeamScore = r1UpScore.last()
                        )

                ),
                PlayInEvent(
                        homeTeam = playInTeamDetailMap[seedsBeforePlayIn[2]]!!.run {
                            PostSeasonTeam(
                                    teamAbbr = team,
                                    displayName = displayName,
                                    logo = logo,
                                    location = location,
                                    rank = 9,
                                    seed = seeds.indexOf(team) + 7,
                                    recordSummaryWin = recordSummaryWin,
                                    recordSummaryLose = recordSummaryLose,
                                    isSurviveToPlayOff = true,
                                    isSurvive = true
                            )
                        },
                        guestTeam = playInTeamDetailMap[seedsBeforePlayIn[3]]!!.run {
                            PostSeasonTeam(
                                    teamAbbr = team,
                                    displayName = displayName,
                                    logo = logo,
                                    location = location,
                                    rank = 10,
                                    seed = seeds.indexOf(team) + 7,
                                    recordSummaryWin = recordSummaryWin,
                                    recordSummaryLose = recordSummaryLose,
                                    isSurviveToPlayOff = true,
                                    isSurvive = true
                            )
                        },
                        result = Result(
                                isHomeTeamWin = r1LowHomeWin,
                                homeTeamScore = r1LowScore.first(),
                                guestTeamScore = r1LowScore.last()
                        )

                ),
                PlayInEvent(
                        homeTeam = playInTeamDetailMap[r2HomeTeam]!!.run {
                            PostSeasonTeam(
                                    teamAbbr = team,
                                    displayName = displayName,
                                    logo = logo,
                                    location = location,
                                    rank = seedsBeforePlayIn.indexOf(team) + 7,
                                    seed = seeds.indexOf(team) + 7,
                                    recordSummaryWin = recordSummaryWin,
                                    recordSummaryLose = recordSummaryLose,
                                    isSurviveToPlayOff = true,
                                    isSurvive = true
                            )
                        },
                        guestTeam = playInTeamDetailMap[r2GuestTeam]!!.run {
                            PostSeasonTeam(
                                    teamAbbr = team,
                                    displayName = displayName,
                                    logo = logo,
                                    location = location,
                                    rank = seedsBeforePlayIn.indexOf(team) + 7,
                                    seed = seeds.indexOf(team) + 7,
                                    recordSummaryWin = recordSummaryWin,
                                    recordSummaryLose = recordSummaryLose,
                                    isSurviveToPlayOff = true,
                                    isSurvive = true
                            )
                        },
                        result = Result(
                                isHomeTeamWin = r2HomeWin,
                                homeTeamScore = r2Score.first(),
                                guestTeamScore = r2Score.last()
                        )

                )
        )

        playInEvents.forEach {
            if (seeds.indexOf(it.homeTeam.teamAbbr) >= 2) {
                it.homeTeam.isSurviveToPlayOff = false
            }
            if (seeds.indexOf(it.guestTeam.teamAbbr) >= 2) {
                it.guestTeam.isSurviveToPlayOff = false
            }
        }

        return playInEvents
    }
}