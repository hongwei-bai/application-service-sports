package com.hongwei.service.nba

import com.hongwei.model.jpa.*
import com.hongwei.model.nba.EventType
import com.hongwei.model.nba.TeamStanding
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class NbaAnalysisService {
    private val logger: Logger = LogManager.getLogger(NbaAnalysisService::class.java)

    @Autowired
    private lateinit var nbaTeamScheduleRepository: NbaTeamScheduleRepository

    @Autowired
    private lateinit var nbaTeamDetailRepository: NbaTeamDetailRepository

    @Autowired
    private lateinit var nbaStandingRepository: NbaStandingRepository

    @Autowired
    private lateinit var nbaScheduleRepository: NbaScheduleRepository

    @Throws(IOException::class)
    fun doAnalysisSeasonStatus() {
        val standingDataDb: NbaStandingEntity = nbaStandingRepository.findLatestStandings()?.firstOrNull() ?: return
        val playOffTeams = listOf(
                standingDataDb.western.filter { it.rank <= 6 }.map { it.teamAbbr },
                standingDataDb.eastern.filter { it.rank <= 6 }.map { it.teamAbbr }
        ).flatten()
        val playInTeams = listOf(
                standingDataDb.western.filter { it.rank in 7..10 }.map { it.teamAbbr },
                standingDataDb.eastern.filter { it.rank in 7..10 }.map { it.teamAbbr }
        ).flatten()
        val tails = listOf(standingDataDb.western.last().teamAbbr, standingDataDb.eastern.last().teamAbbr)

        val playOffTeamSchedules = playOffTeams.map { nbaTeamScheduleRepository.findScheduleByTeam(it) }
        val playInTeamSchedules = playInTeams.map { nbaTeamScheduleRepository.findScheduleByTeam(it) }
        val tailTeamSchedules = tails.map { nbaTeamScheduleRepository.findScheduleByTeam(it) }

        val leagueEventType: EventType? = when {
            numberOfTailTeamsHasIncomingPreMatch(tailTeamSchedules) > 0 -> EventType.PreSeason
            numberOfTailTeamsHasIncomingSeasonMatch(tailTeamSchedules) > 0 -> EventType.Season
            numberOfPlayInTeamsHasIncomingPlayInMatch(playInTeamSchedules) > 0 -> EventType.PlayIn
            numberOfPlayOffTeamsHasIncomingPlayOffMatch(playOffTeamSchedules) > 0 -> {
                when {
                    numberOfTeamsPlayedGrandFinal(playOffTeamSchedules) > 0 -> EventType.PlayOffGrandFinal
                    numberOfTeamsPlayedConferenceFinal(playOffTeamSchedules) > 0 -> EventType.PlayOffConferenceFinal
                    numberOfTeamsPlayedRound2(playOffTeamSchedules) > 0 -> EventType.PlayOffRound2
                    numberOfTeamsPlayedRound1(playOffTeamSchedules) > 0 -> EventType.PlayOffRound1
                    else -> null
                }
            }
            else -> null
        }
        logger.debug("leagueEventType: $leagueEventType")

//        doAnalysisPlayIn(standingDataDb.western, playInTeamSchedules)
        doAnalysisFinal(listOf(playOffTeamSchedules, playInTeamSchedules).flatten())
    }

    private fun doAnalysisFinal(playOffTeamSchedules: List<NbaTeamScheduleEntity?>) {
        val debugTimes = playOffTeamSchedules.filter {
            it?.events?.any { teamEvent -> teamEvent.eventType == EventType.PlayOffGrandFinal.name } == true
        }.size
        logger.debug("doAnalysisFinal run once, x$debugTimes")

        playOffTeamSchedules.filter {
            it?.events?.any { teamEvent -> teamEvent.eventType == EventType.PlayOffGrandFinal.name } == true
        }.forEach {
            val team = it?.team
            val score = it?.events?.filter { teamEvent ->
                teamEvent.eventType == EventType.PlayOffGrandFinal.name && teamEvent.result?.winLossSymbol == "W"
            }?.size
            logger.debug("Team: $team, score: $score")
        }
    }

    private fun doAnalysisPlayIn(conferenceStandings: List<TeamStanding>, playInTeamSchedules: List<NbaTeamScheduleEntity?>) {
        fun numberOfPlayInMatchPlayed(rank: Int): Int = playInTeamSchedules.firstOrNull {
            it?.team == conferenceStandings.first { standing -> standing.rank == rank }.teamAbbr
        }?.events?.filter {
            it.eventType == EventType.PlayIn.name && it.result != null
        }?.size ?: 0

        fun numberOfPlayInMatchAhead(rank: Int): Int = playInTeamSchedules.firstOrNull {
            it?.team == conferenceStandings.first { standing -> standing.rank == rank }.teamAbbr
        }?.events?.filter {
            it.eventType == EventType.PlayIn.name && it.result == null
        }?.size ?: 0

        val upperTeams = conferenceStandings.filter { it.rank == 7 || it.rank == 8 }.map { it.teamAbbr }
        val lowerTeams = conferenceStandings.filter { it.rank == 9 || it.rank == 10 }.map { it.teamAbbr }

        val numberOfMatchPlayed = numberOfPlayInMatchPlayed(7) + numberOfPlayInMatchPlayed(8) +
                numberOfPlayInMatchPlayed(9) + numberOfPlayInMatchPlayed(10)
        val numberOfMatchAhead = numberOfPlayInMatchAhead(7) + numberOfPlayInMatchAhead(8) +
                numberOfPlayInMatchAhead(9) + numberOfPlayInMatchAhead(10)
        if (numberOfMatchPlayed == 6 && numberOfMatchAhead == 0) {
            // Finished
            val seed7AndSeed9 = playInTeamSchedules.filter { it?.events?.filter { teamEvent -> teamEvent.result?.winLossSymbol == "W" }?.size == 1 }
            val seed7 = seed7AndSeed9.first { upperTeams.contains(it?.team) }
            val seed8 = playInTeamSchedules.first { it?.events?.filter { teamEvent -> teamEvent.result?.winLossSymbol == "W" }?.size == 2 }
            val seed9 = seed7AndSeed9.first { lowerTeams.contains(it?.team) }
            val seed10 = playInTeamSchedules.first { it?.events?.filter { teamEvent -> teamEvent.result?.winLossSymbol == "L" }?.size == 2 }
            logger.debug("seed7: $seed7")
            logger.debug("seed8: $seed8")
            logger.debug("seed9: $seed9")
            logger.debug("seed10: $seed10")
        } else if (numberOfMatchPlayed == 4 && numberOfMatchAhead == 2) {
            // R2 Ongoing
        } else if (numberOfMatchPlayed == 4 && numberOfMatchAhead == 0) {
            // R2 Ongoing
        } else if (numberOfMatchPlayed == 2 && numberOfMatchAhead == 2) {
            // R1 Ongoing
        } else if (numberOfMatchPlayed == 0 && numberOfMatchAhead == 4) {
            // R1 Not Start
        } else {
            // Unknown
        }
    }

    private fun numberOfTailTeamsHasIncomingPreMatch(tailTeamSchedules: List<NbaTeamScheduleEntity?>) = tailTeamSchedules.filter { it ->
        it?.events?.any { it.result == null && it.eventType == EventType.PreSeason.name } == true
    }.size

    private fun numberOfTailTeamsHasIncomingSeasonMatch(tailTeamSchedules: List<NbaTeamScheduleEntity?>) = tailTeamSchedules.filter { it ->
        it?.events?.any { it.result == null && it.eventType == EventType.Season.name } == true
    }.size

    private fun numberOfPlayInTeamsHasIncomingPlayInMatch(playInTeamSchedules: List<NbaTeamScheduleEntity?>) = playInTeamSchedules.filter { it ->
        it?.events?.any { it.result == null && it.eventType == EventType.PlayIn.name } == true
    }.size

    private fun numberOfPlayOffTeamsHasIncomingPlayOffMatch(playOffTeamSchedules: List<NbaTeamScheduleEntity?>) = playOffTeamSchedules.filter { it ->
        it?.events?.any { it.result == null && EventType.isPlayOff(it.eventType) } == true
    }.size

    private fun numberOfTeamsPlayedGrandFinal(playOffTeamSchedules: List<NbaTeamScheduleEntity?>) = playOffTeamSchedules.filter { it ->
        it?.events?.any { it.result != null && it.eventType == EventType.PlayOffGrandFinal.name } == true
    }.size

    private fun numberOfTeamsPlayedConferenceFinal(playOffTeamSchedules: List<NbaTeamScheduleEntity?>) = playOffTeamSchedules.filter { it ->
        it?.events?.any { it.result != null && it.eventType == EventType.PlayOffConferenceFinal.name } == true
    }.size

    private fun numberOfTeamsPlayedRound2(playOffTeamSchedules: List<NbaTeamScheduleEntity?>) = playOffTeamSchedules.filter { it ->
        it?.events?.any { it.result != null && it.eventType == EventType.PlayOffRound2.name } == true
    }.size

    private fun numberOfTeamsPlayedRound1(playOffTeamSchedules: List<NbaTeamScheduleEntity?>) = playOffTeamSchedules.filter { it ->
        it?.events?.any { it.result != null && it.eventType == EventType.PlayOffRound1.name } == true
    }.size

}