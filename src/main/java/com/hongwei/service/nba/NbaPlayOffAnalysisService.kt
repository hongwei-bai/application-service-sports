package com.hongwei.service.nba

import com.hongwei.model.jpa.nba.*
import com.hongwei.model.nba.EventType
import com.hongwei.model.nba.PlayInEvent
import com.hongwei.model.nba.PlayOffSeries
import com.hongwei.model.nba.TeamStanding
import com.hongwei.model.nba.espn.mapper.TeamScheduleMapper
import com.hongwei.model.nba.mapper.PostSeasonTeamMapper
import com.hongwei.util.TimeStampUtil
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class NbaPlayOffAnalysisService {
    private val logger: Logger = LogManager.getLogger(NbaPlayOffAnalysisService::class.java)

    @Autowired
    private lateinit var nbaStandingRepository: NbaStandingRepository

    @Autowired
    private lateinit var nbaTeamScheduleRepository: NbaTeamScheduleRepository

    @Autowired
    private lateinit var nbaPlayInRepository: NbaPlayInRepository

    @Autowired
    private lateinit var nbaPostSeasonRepository: NbaPostSeasonRepository

    @Autowired
    private lateinit var nbaTeamDetailRepository: NbaTeamDetailRepository

    // Pre-requests: Standing in db, playIn in db, teamSchedule in db
    @Throws(IOException::class)
    fun fetchPlayOff(): NbaPostSeasonEntity? {
        val postSeasonEntityDb = nbaPostSeasonRepository.findPostSeason().firstOrNull()
        nbaStandingRepository.findLatestStandings()?.firstOrNull()?.let { standing ->
            val playInEvents = nbaPlayInRepository.findPlayIn().firstOrNull()
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
                        dataVersion = TimeStampUtil.getTimeVersionWithMinute(),
                        westernPlayInEvents = nbaPlayInRepository.findPlayIn().firstOrNull()?.westernPlayInEvents
                                ?: emptyList(),
                        easternPlayInEvents = nbaPlayInRepository.findPlayIn().firstOrNull()?.easternPlayInEvents
                                ?: emptyList(),
                        westernRound1Series = westernSeries.subList(0, 4),
                        westernRound2Series = westernSeries.subList(4, 6),
                        westernConferenceFinal = westernSeries.last(),
                        easternRound1Series = easternSeries.subList(0, 4),
                        easternRound2Series = easternSeries.subList(4, 6),
                        easternConferenceFinal = easternSeries.last(),
                        final = finalSeries
                )
                if (postSeasonEntityDb == postSeasonEntity) {
                    return postSeasonEntityDb
                }
                nbaPostSeasonRepository.save(postSeasonEntity)
                return postSeasonEntity
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
            var westernChampionWins = 0
            var easternChampionWins = 0
            westernChampionFinalSchedule.filter { it.eventType == EventType.PlayOffGrandFinal.name }.run {
                westernChampionWins = filter { it.result?.isWin == true }.size
                easternChampionWins = filter { it.result?.isWin == false }.size
            }
            return PlayOffSeries(
                    team1 = PostSeasonTeamMapper.map(westernChampion, westernChampionRank, westernChampionSeed),
                    team2 = PostSeasonTeamMapper.map(easternChampion, easternChampionRank, easternChampionSeed),
                    team1Wins = westernChampionWins,
                    team2Wins = easternChampionWins,
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
                var highRankTeamWins = 0
                var lowRankTeamWins = 0
                highRankTeamSchedule.events.filter { it.eventType == EventType.PlayOffRound1.name }.run {
                    highRankTeamWins = filter { it.result?.isWin == true }.size
                    lowRankTeamWins = filter { it.result?.isWin == false }.size
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
                round1Events[i] = PlayOffSeries(
                        team1 = PostSeasonTeamMapper.map(highRankTeamDetail, r1UpRanks[i], r1UpSeeds[i]),
                        team2 = PostSeasonTeamMapper.map(lowRankTeamDetail, r1DownRanks[i], r1DownSeeds[i]),
                        team1Wins = highRankTeamWins,
                        team2Wins = lowRankTeamWins,
                        events = highRankTeamSchedule.events.filter { it.eventType == EventType.PlayOffRound1.name }.map { TeamScheduleMapper.teamEventMapToEvent(it, highRankTeamDetail) }
                )
            }
        }

        val round2Events = Array<PlayOffSeries?>(2) { null }
        val round2Winners = Array<NbaTeamDetailEntity?>(2) { null }
        val conferenceFinalTeamSeeds = mutableListOf<Int>()
        val conferenceFinalTeamRanks = mutableListOf<Int>()
        round1Winners.filterIndexed { i, _ -> i % 2 == 0 }.forEachIndexed { j, teamDetail ->
            val i = j * 2
            val teamSchedule = nbaTeamScheduleRepository.findScheduleByTeam(teamDetail?.team)
            val opponentDetail = round1Winners[i + 1]
            if (teamDetail != null && opponentDetail != null && teamSchedule != null) {
                var team1Wins = 0
                var team2Wins = 0
                teamSchedule.events.filter { it.eventType == EventType.PlayOffRound2.name }.run {
                    team1Wins = filter { it.result?.isWin == true }.size
                    team2Wins = filter { it.result?.isWin == false }.size
                    when {
                        team1Wins == 4 -> {
                            round2Winners[j] = teamDetail
                            conferenceFinalTeamSeeds.add(r2TeamSeeds[i])
                            conferenceFinalTeamRanks.add(r2TeamRanks[i])
                            round2Events[j]?.team2?.isSurvive = false
                        }
                        team2Wins == 4 -> {
                            round2Winners[j] = opponentDetail
                            conferenceFinalTeamSeeds.add(r2TeamSeeds[i + 1])
                            conferenceFinalTeamRanks.add(r2TeamRanks[i + 1])
                            round2Events[j]?.team2?.isSurvive = false
                        }
                        else -> {
                            // No-Op
                        }
                    }
                }
                round2Events[j] = PlayOffSeries(
                        team1 = PostSeasonTeamMapper.map(teamDetail, r2TeamRanks[i], r2TeamSeeds[i]),
                        team2 = PostSeasonTeamMapper.map(opponentDetail, r2TeamRanks[i + 1], r2TeamSeeds[i + 1]),
                        team1Wins = team1Wins,
                        team2Wins = team2Wins,
                        events = teamSchedule.events.filter { it.eventType == EventType.PlayOffRound2.name }.map { TeamScheduleMapper.teamEventMapToEvent(it, teamDetail) }
                )
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
                var team1Wins = 0
                var team2Wins = 0
                teamSchedule.events.filter { it.eventType == EventType.PlayOffConferenceFinal.name }.run {
                    team1Wins = filter { it.result?.isWin == true }.size
                    team2Wins = filter { it.result?.isWin == false }.size
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
                conferenceEvent = PlayOffSeries(
                        team1 = PostSeasonTeamMapper.map(teamDetail, conferenceFinalTeamRanks.first(), conferenceFinalTeamSeeds.first()),
                        team2 = PostSeasonTeamMapper.map(opponentDetail, conferenceFinalTeamRanks.last(), conferenceFinalTeamSeeds.last()),
                        team1Wins = team1Wins,
                        team2Wins = team2Wins,
                        events = teamSchedule.events.filter { it.eventType == EventType.PlayOffRound2.name }.map { TeamScheduleMapper.teamEventMapToEvent(it, teamDetail) }
                )
            }
        }
        return Triple(listOf(round1Events.toList(), round2Events.toList(), listOf(conferenceEvent)).flatten(),
                conferenceChampion, Pair(conferenceChampionSeed, conferenceChampionRank))
    }
}