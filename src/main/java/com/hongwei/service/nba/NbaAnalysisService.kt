package com.hongwei.service.nba

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hongwei.model.jpa.nba.*
import com.hongwei.model.nba.EventType
import com.hongwei.model.nba.PostSeason
import com.hongwei.model.nba.espn.TransactionSource
import com.hongwei.model.nba.espn.mapper.NbaTransactionsMapper
import com.hongwei.model.nba.mapper.PostSeasonMapper
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
	private lateinit var nbaStandingRepository: NbaStandingRepository

	@Autowired
	private lateinit var nbaTransactionsRepository: NbaTransactionsRepository

	@Autowired
	private lateinit var nbaPlayOffAnalysisService: NbaPlayOffAnalysisService

	@Autowired
	private lateinit var nbaPlayInAnalysisService: NbaPlayInAnalysisService

	@Autowired
	private lateinit var espnCurlService: EspnCurlService

	@Throws(IOException::class)
	fun saveTransactions(): NbaTransactionsEntity? {
		val transactionsDb = nbaTransactionsRepository.findTransactions()
		val jsonString = espnCurlService.getTransactionsJson(espnCurlService.getTransactions().toString())
		val transactionsSource = Gson().fromJson<List<TransactionSource>>(jsonString,
			object : TypeToken<List<TransactionSource>?>() {}.type)
		val transactionsEntity = NbaTransactionsMapper.map(transactionsSource)
		if (transactionsEntity == transactionsDb) {
			return transactionsDb
		}
		nbaTransactionsRepository.deleteAll()
		nbaTransactionsRepository.save(transactionsEntity)
		return transactionsEntity
	}

	@Throws(IOException::class)
	fun doAnalysisPostSeason(): PostSeason? {
		return when (val stage = doAnalysisSeasonStatus()) {
			EventType.PlayOffRound1,
			EventType.PlayOffRound2,
			EventType.PlayOffConferenceFinal,
			EventType.PlayOffGrandFinal -> {
				nbaPlayInAnalysisService.fetchPlayIn(false)
				PostSeasonMapper.map(stage, nbaPlayOffAnalysisService.fetchPlayOff())
			}
			EventType.PlayIn -> {
				PostSeasonMapper.mapPlayIn(stage, nbaPlayInAnalysisService.fetchPlayIn(true))
			}
			else -> null
		}
	}

	@Throws(IOException::class)
	fun doAnalysisSeasonStatus(): EventType? {
		val standingDataDb: NbaStandingEntity = nbaStandingRepository.findLatestStandings()?.firstOrNull()
			?: return null
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

		return when {
			numberOfTailTeamsHasIncomingPreMatch(tailTeamSchedules) > 0 -> EventType.PreSeason
			numberOfTailTeamsHasIncomingSeasonMatch(tailTeamSchedules) > 0 -> EventType.Season
			numberOfPlayInTeamsHasIncomingPlayInMatch(playInTeamSchedules) > 0 -> EventType.PlayIn
			numberOfPlayOffTeamsHasIncomingPlayOffMatch(playOffTeamSchedules) > 0 -> {
				when {
					numberOfTeamsPlayGrandFinal(playOffTeamSchedules) > 0 -> EventType.PlayOffGrandFinal
					numberOfTeamsPlayConferenceFinal(playOffTeamSchedules) > 0 -> EventType.PlayOffConferenceFinal
					numberOfTeamsPlayRound2(playOffTeamSchedules) > 0 -> EventType.PlayOffRound2
					numberOfTeamsPlayRound1(playOffTeamSchedules) > 0 -> EventType.PlayOffRound1
					else -> null
				}
			}
			else -> null
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

	private fun numberOfTeamsPlayGrandFinal(playOffTeamSchedules: List<NbaTeamScheduleEntity?>) = playOffTeamSchedules.filter { it ->
		it?.events?.any { it.eventType == EventType.PlayOffGrandFinal.name } == true
	}.size

	private fun numberOfTeamsPlayConferenceFinal(playOffTeamSchedules: List<NbaTeamScheduleEntity?>) = playOffTeamSchedules.filter { it ->
		it?.events?.any { it.eventType == EventType.PlayOffConferenceFinal.name } == true
	}.size

	private fun numberOfTeamsPlayRound2(playOffTeamSchedules: List<NbaTeamScheduleEntity?>) = playOffTeamSchedules.filter { it ->
		it?.events?.any { it.eventType == EventType.PlayOffRound2.name } == true
	}.size

	private fun numberOfTeamsPlayRound1(playOffTeamSchedules: List<NbaTeamScheduleEntity?>) = playOffTeamSchedules.filter { it ->
		it?.events?.any { it.eventType == EventType.PlayOffRound1.name } == true
	}.size

}