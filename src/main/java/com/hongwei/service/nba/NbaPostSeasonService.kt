package com.hongwei.service.nba

import com.google.gson.Gson
import com.hongwei.model.jpa.*
import com.hongwei.model.nba.*
import com.hongwei.model.nba.espn.TeamDetailSource
import com.hongwei.model.nba.espn.TeamScheduleSource
import com.hongwei.model.nba.espn.mapper.TeamDetailMapper
import com.hongwei.model.nba.espn.mapper.TeamScheduleMapper
import com.hongwei.model.nba.mapper.PostSeasonTeamMapper
import com.hongwei.util.TimeStampUtil
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
    private lateinit var nbaTeamScheduleRepository: NbaTeamScheduleRepository

    @Autowired
    private lateinit var nbaPostSeasonArchivedRepository: NbaPostSeasonArchivedRepository

    @Autowired
    private lateinit var nbaPostSeasonRepository: NbaPostSeasonRepository

    @Autowired
    private lateinit var nbaTeamDetailRepository: NbaTeamDetailRepository

    @Autowired
    private lateinit var nbaCurlService: EspnCurlService

    // Pre-requests: Standing in db, playIn in db, teamSchedule in db
    @Throws(IOException::class)
    fun fetchPlayOff(dataVersionBase: Int?): NbaPostSeasonArchivedEntity? {
        nbaStandingRepository.findLatestStandings()?.firstOrNull()?.let { standing ->
            val playInEvents = nbaPostSeasonArchivedRepository.findPostSeasonArchived().firstOrNull()
            playInEvents?.let {
                val (westernSeries, westernChampion, westernChampionSeedAndRank) = analysisConferencePlayOff(standing.western, it.westernPlayInEvents)
                val (easternSeries, easternChampion, easternChampionSeedAndRank) = analysisConferencePlayOff(standing.eastern, it.easternPlayInEvents)
                val finalSeries = if (westernChampion != null && easternChampion != null) {
                    if (westernSeries.last() != null && easternSeries.last() != null) {
                        analysisFinal(westernChampion, westernChampionSeedAndRank.first, westernChampionSeedAndRank.second,
                                easternChampion, easternChampionSeedAndRank.first, easternChampionSeedAndRank.second)
                    } else null
                } else null
                val postSeasonEntity = NbaPostSeasonEntity(
                        dataVersion = TimeStampUtil.getTimeVersionWithDayAndDataVersion(dataVersion = dataVersionBase).toLong(),
                        westernRound1Series = westernSeries.subList(0, 4),
                        westernRound2Series = westernSeries.subList(4, 6),
                        westernConferenceFinal = westernSeries.last(),
                        easternRound1Series = easternSeries.subList(0, 4),
                        easternRound2Series = easternSeries.subList(4, 6),
                        easternConferenceFinal = easternSeries.last(),
                        final = finalSeries
                )
                nbaPostSeasonRepository.save(postSeasonEntity)
            }
        }
        return null
    }

    private fun analysisFinal(westernChampion: NbaTeamDetailEntity, westernChampionSeed: Int, westernChampionRank: Int,
                              easternChampion: NbaTeamDetailEntity, easternChampionSeed: Int, easternChampionRank: Int): PlayOffSeries? {
        val westernChampionFinalSchedule = nbaTeamScheduleRepository.findScheduleByTeam(westernChampion.team)?.events
                ?.filter { it.eventType == EventType.PlayOffGrandFinal.name }
        val easternChampionFinalSchedule = nbaTeamScheduleRepository.findScheduleByTeam(easternChampion.team)?.events
                ?.filter { it.eventType == EventType.PlayOffGrandFinal.name }
        if (westernChampionFinalSchedule != null && easternChampionFinalSchedule != null) {
            return PlayOffSeries(
                    team1 = PostSeasonTeamMapper.map(westernChampion, westernChampionRank, westernChampionSeed),
                    team2 = PostSeasonTeamMapper.map(easternChampion, easternChampionRank, easternChampionSeed),
                    events = westernChampionFinalSchedule.map {
                        TeamScheduleMapper.teamEventMapToEvent(it, westernChampion)
                    }
            )
        }
        return null
    }

    private fun analysisConferencePlayOff(conferenceStandings: List<TeamStanding>, playInEvents: List<PlayInEvent?>): Triple<List<PlayOffSeries?>, NbaTeamDetailEntity?, Pair<Int, Int>> {
        val seed7 = if (playInEvents.first()?.result?.isHomeTeamWin == true) playInEvents.first()?.homeTeam?.teamAbbr else playInEvents.first()?.guestTeam?.teamAbbr
        val seed8 = if (playInEvents.last()?.result?.isHomeTeamWin == true) playInEvents.last()?.homeTeam?.teamAbbr else playInEvents.last()?.guestTeam?.teamAbbr
        val r1HighRankTeams = listOf(conferenceStandings[0].teamAbbr, conferenceStandings[3].teamAbbr, conferenceStandings[2].teamAbbr, conferenceStandings[1].teamAbbr)
        val r1LowRankTeams = listOf(seed8, conferenceStandings[4].teamAbbr, conferenceStandings[5].teamAbbr, seed7)
        val seed7Rank = if (playInEvents.first()?.result?.isHomeTeamWin == true) 7 else 8
        val seed8Rank = if (playInEvents.last()?.result?.isHomeTeamWin == true) {
            if (playInEvents.first()?.result?.isHomeTeamWin == true) 8 else 7
        } else {
            if (playInEvents[1]?.result?.isHomeTeamWin == true) 9 else 10
        }

        val r1UpSeeds = listOf(1, 4, 3, 2)
        val r1DownSeeds = listOf(8, 5, 6, 7)
        val r1UpRanks = listOf(1, 4, 3, 2)
        val r1DownRanks = listOf(seed8Rank, 5, 6, seed7Rank)

        val round1Events = Array<PlayOffSeries?>(4) { null }
        val round1Winners = Array<NbaTeamDetailEntity?>(4) { null }
        val r2TeamSeeds = mutableListOf<Int>()
        val r2TeamRanks = mutableListOf<Int>()
        r1HighRankTeams.forEachIndexed { i, team ->
            val highRankTeamDetail = nbaTeamDetailRepository.findTeamDetail(team)
            val highRankTeamSchedule = nbaTeamScheduleRepository.findScheduleByTeam(team)
            val lowRankTeamDetail = nbaTeamDetailRepository.findTeamDetail(r1LowRankTeams[i])
            if (highRankTeamDetail != null && highRankTeamSchedule != null && lowRankTeamDetail != null) {
                round1Events[i] = PlayOffSeries(
                        team1 = PostSeasonTeamMapper.map(highRankTeamDetail, r1UpRanks[i], r1UpSeeds[i]),
                        team2 = PostSeasonTeamMapper.map(lowRankTeamDetail, r1DownRanks[i], r1DownSeeds[i]),
                        events = highRankTeamSchedule.events.filter { it.eventType == EventType.PlayOffRound1.name }.map { TeamScheduleMapper.teamEventMapToEvent(it, highRankTeamDetail) }
                )
                highRankTeamSchedule.events.filter { it.eventType == EventType.PlayOffRound1.name }.run {
                    val highRankTeamWins = filter { it.result?.isWin == true }.size
                    val lowRankTeamWins = filter { it.result?.isWin == false }.size
                    when {
                        highRankTeamWins == 4 -> {
                            round1Winners[i] = highRankTeamDetail
                            r2TeamSeeds.add(r1UpSeeds[i])
                            r2TeamRanks.add(r1UpRanks[i])
                            round1Events[i]?.team2?.isSurvive = false
                        }
                        lowRankTeamWins == 4 -> {
                            round1Winners[i] = lowRankTeamDetail
                            r2TeamSeeds.add(r1DownSeeds[i])
                            r2TeamRanks.add(r1DownRanks[i])
                            round1Events[i]?.team1?.isSurvive = false
                        }
                        else -> {
                            // No-Op
                        }
                    }
                }
            }
        }

        val round2Events = Array<PlayOffSeries?>(2) { null }
        val round2Winners = Array<NbaTeamDetailEntity?>(2) { null }
        val conferenceFinalTeamSeeds = mutableListOf<Int>()
        val conferenceFinalTeamRanks = mutableListOf<Int>()
        round1Winners.filterIndexed { i, _ -> i % 2 == 0 }.forEachIndexed { i, teamDetail ->
            val teamSchedule = nbaTeamScheduleRepository.findScheduleByTeam(teamDetail?.team)
            val opponentDetail = round1Winners[i + 1]
            if (teamDetail != null && opponentDetail != null && teamSchedule != null) {
                round2Events[i] = PlayOffSeries(
                        team1 = PostSeasonTeamMapper.map(teamDetail, r2TeamRanks[i], r2TeamSeeds[i]),
                        team2 = PostSeasonTeamMapper.map(opponentDetail, r2TeamRanks[i + 1], r2TeamSeeds[i + 1]),
                        events = teamSchedule.events.filter { it.eventType == EventType.PlayOffRound2.name }.map { TeamScheduleMapper.teamEventMapToEvent(it, teamDetail) }
                )
                teamSchedule.events.filter { it.eventType == EventType.PlayOffRound2.name }.run {
                    val team1Wins = filter { it.result?.isWin == true }.size
                    val team2Wins = filter { it.result?.isWin == false }.size
                    when {
                        team1Wins == 4 -> {
                            round2Winners[i] = teamDetail
                            conferenceFinalTeamSeeds.add(r2TeamSeeds[i])
                            conferenceFinalTeamRanks.add(r2TeamRanks[i])
                            round2Events[i]?.team2?.isSurvive = false
                        }
                        team2Wins == 4 -> {
                            round2Winners[i] = opponentDetail
                            conferenceFinalTeamSeeds.add(r2TeamSeeds[i + 1])
                            conferenceFinalTeamRanks.add(r2TeamRanks[i + 1])
                            round2Events[i]?.team2?.isSurvive = false
                        }
                        else -> {
                            // No-Op
                        }
                    }
                }
            }
        }

        var conferenceEvent: PlayOffSeries? = null
        var conferenceChampion: NbaTeamDetailEntity? = null
        var conferenceChampionSeed = 0
        var conferenceChampionRank = 0
        round2Winners.firstOrNull()?.let { teamDetail ->
            val teamSchedule = nbaTeamScheduleRepository.findScheduleByTeam(teamDetail.team)
            val opponentDetail = round2Winners.last()
            if (opponentDetail != null && teamSchedule != null) {
                conferenceEvent = PlayOffSeries(
                        team1 = PostSeasonTeamMapper.map(teamDetail, conferenceFinalTeamRanks.first(), conferenceFinalTeamSeeds.first()),
                        team2 = PostSeasonTeamMapper.map(opponentDetail, conferenceFinalTeamRanks.last(), conferenceFinalTeamSeeds.last()),
                        events = teamSchedule.events.filter { it.eventType == EventType.PlayOffRound2.name }.map { TeamScheduleMapper.teamEventMapToEvent(it, teamDetail) }
                )
                teamSchedule.events.filter { it.eventType == EventType.PlayOffConferenceFinal.name }.run {
                    val team1Wins = filter { it.result?.isWin == true }.size
                    val team2Wins = filter { it.result?.isWin == false }.size
                    when {
                        team1Wins == 4 -> {
                            conferenceChampion = teamDetail
                            conferenceChampionSeed = conferenceFinalTeamSeeds.first()
                            conferenceChampionRank = conferenceFinalTeamRanks.first()
                            conferenceEvent?.team2?.isSurvive = false
                        }
                        team2Wins == 4 -> {
                            conferenceChampion = opponentDetail
                            conferenceChampionSeed = conferenceFinalTeamSeeds.last()
                            conferenceChampionRank = conferenceFinalTeamRanks.last()
                            conferenceEvent?.team1?.isSurvive = false
                        }
                        else -> {
                            // No-Op
                        }
                    }
                }
            }
        }
        return Triple(listOf(round1Events.toList(), round2Events.toList(), listOf(conferenceEvent)).flatten(),
                conferenceChampion, Pair(conferenceChampionSeed, conferenceChampionRank))
    }

    // Pre-requests: Standing in db
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