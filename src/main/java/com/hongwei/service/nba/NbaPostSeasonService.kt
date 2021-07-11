package com.hongwei.service.nba

import com.google.gson.Gson
import com.hongwei.model.jpa.*
import com.hongwei.model.nba.EventType
import com.hongwei.model.nba.PlayInEvent
import com.hongwei.model.nba.PostSeasonTeam
import com.hongwei.model.nba.Result
import com.hongwei.model.nba.TeamStanding
import com.hongwei.model.nba.espn.TeamDetailSource
import com.hongwei.model.nba.espn.TeamScheduleSource
import com.hongwei.model.nba.espn.mapper.TeamDetailMapper
import com.hongwei.model.nba.espn.mapper.TeamScheduleMapper
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class NbaPostSeasonService {
    private val logger: Logger = LogManager.getLogger(NbaPostSeasonService::class.java)

    @Autowired
    private lateinit var nbaStandingRepository: NbaStandingRepository

    @Autowired
    private lateinit var nbaPostSeasonArchivedRepository: NbaPostSeasonArchivedRepository

    @Autowired
    private lateinit var nbaCurlService: EspnCurlService

    @Throws(IOException::class)
    fun fetchPlayIn(currentStage: EventType, dataVersionBase: Int?): NbaPostSeasonArchivedEntity? {
        when (currentStage) {
            EventType.PlayOffRound1,
            EventType.PlayOffRound2,
            EventType.PlayOffConferenceFinal,
            EventType.PlayOffGrandFinal -> {
                val postSeasonArchivedEntity: NbaPostSeasonArchivedEntity? =
                        nbaPostSeasonArchivedRepository.findPostSeasonArchived().firstOrNull()
                if (postSeasonArchivedEntity?.westernPlayInEvents?.isNotEmpty() == true && postSeasonArchivedEntity.easternPlayInEvents.isNotEmpty()) {
                    return postSeasonArchivedEntity
                } else {
                    nbaStandingRepository.findLatestStandings()?.firstOrNull()?.let { standing ->
                        val westernSeeds = analysisConferencePlayIn(standing.western, dataVersionBase)
                        val easternSeeds = analysisConferencePlayIn(standing.eastern, dataVersionBase)
                        val entity = NbaPostSeasonArchivedEntity(
                                westernPlayInEvents = westernSeeds,
                                easternPlayInEvents = easternSeeds
                        )
                        nbaPostSeasonArchivedRepository.save(entity)
                        return entity
                    }
                }
            }
            EventType.PlayIn -> {

            }
            else -> {
                // No-Op
            }
        }
        return null
    }

    private fun analysisConferencePlayIn(conferenceStandings: List<TeamStanding>, dataVersionBase: Int?): List<PlayInEvent?> {
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
            val teamScheduleSourceObj =
                    nbaCurlService.getTeamScheduleJson(doc, dataVersionBase ?: 0)
            val teamScheduleEntity: NbaTeamScheduleEntity? = teamScheduleSourceObj?.let {
                TeamScheduleMapper.map(playInTeam.teamAbbr, Gson().fromJson(teamScheduleSourceObj, TeamScheduleSource::class.java))
            }
            teamScheduleEntity?.run {
                if (events.size == 1) {
                    when (events.first().result?.winLossSymbol) {
                        "W" -> {
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
                        "L" -> {
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
                        events.first().result?.winLossSymbol == "W" && events.last().result?.winLossSymbol == "W" -> {
                            seeds[1] = team.toLowerCase()
                            r2HomeWin = false
                            r2Score[0] = events.last().result?.opponentTeamScore ?: 0
                            r2Score[1] = events.last().result?.currentTeamScore ?: 0
                        }
                        events.first().result?.winLossSymbol == "W" && events.last().result?.winLossSymbol == "L" -> seeds[2] = team
                        events.first().result?.winLossSymbol == "L" && events.last().result?.winLossSymbol == "W" -> {
                            seeds[1] = team.toLowerCase()
                            r2HomeWin = true
                            r2Score[0] = events.last().result?.currentTeamScore ?: 0
                            r2Score[1] = events.last().result?.opponentTeamScore ?: 0
                        }
                        events.first().result?.winLossSymbol == "L" && events.last().result?.winLossSymbol == "L" -> seeds[2] = team
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
                                    isEliminatedBeforeFinal = false
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
                                    isEliminatedBeforeFinal = false
                            )
                        },
                        result = Result(
                                homeTeamWinLossSymbol = if (r1UpHomeWin) "W" else "L",
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
                                    isEliminatedBeforeFinal = false
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
                                    isEliminatedBeforeFinal = false
                            )
                        },
                        result = Result(
                                homeTeamWinLossSymbol = if (r1LowHomeWin) "W" else "L",
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
                                    isEliminatedBeforeFinal = false
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
                                    isEliminatedBeforeFinal = false
                            )
                        },
                        result = Result(
                                homeTeamWinLossSymbol = if (r2HomeWin) "W" else "L",
                                homeTeamScore = r2Score.first(),
                                guestTeamScore = r2Score.last()
                        )

                )
        )

        playInEvents.forEach {
            if (seeds.indexOf(it.homeTeam.teamAbbr) >= 2) {
                it.homeTeam.isEliminatedBeforeFinal = true
            }
            if (seeds.indexOf(it.guestTeam.teamAbbr) >= 2) {
                it.guestTeam.isEliminatedBeforeFinal = true
            }
        }

        return playInEvents
    }
}