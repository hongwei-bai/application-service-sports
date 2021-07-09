package com.hongwei.service.nba

import com.hongwei.model.jpa.*
import com.hongwei.model.nba.EventType
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
                standingDataDb.western.filter { it.rank == 7 || it.rank == 8 }.map { it.teamAbbr },
                standingDataDb.eastern.filter { it.rank == 7 || it.rank == 8 }.map { it.teamAbbr }
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