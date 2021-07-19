package com.hongwei.service.nba

import com.hongwei.constants.NoContent
import com.hongwei.model.jpa.nba.*
import com.hongwei.model.nba.Schedule
import com.hongwei.model.nba.Standing
import com.hongwei.model.nba.Team
import com.hongwei.model.nba.TeamSchedule
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class NbaService {
	private val logger: Logger = LogManager.getLogger(NbaService::class.java)

	@Autowired
	private lateinit var nbaTeamScheduleRepository: NbaTeamScheduleRepository

	@Autowired
	private lateinit var nbaTeamDetailRepository: NbaTeamDetailRepository

	@Autowired
	private lateinit var nbaStandingRepository: NbaStandingRepository

	@Autowired
	private lateinit var nbaScheduleRepository: NbaScheduleRepository

	@Autowired
	private lateinit var nbaTransactionsRepository: NbaTransactionsRepository

	@Throws(IOException::class)
	fun getScheduleByTeam(team: String, currentDataVersion: Long): TeamSchedule? {
		val teamScheduleDb: NbaTeamScheduleEntity? = nbaTeamScheduleRepository.findScheduleByTeam(team)
		val teamDetailDb: NbaTeamDetailEntity? = nbaTeamDetailRepository.findTeamDetail(team)
		val teamDb: Team? = teamDetailDb?.run {
			Team(
				abbrev = team,
				displayName = teamDetailDb.displayName,
				logo = teamDetailDb.logo,
				location = teamDetailDb.location
			)
		}
		return if (teamScheduleDb == null || teamDb == null) {
			throw NoContent
		} else if (teamScheduleDb.dataVersion > currentDataVersion) {
			TeamSchedule(teamScheduleDb.dataVersion, teamDb, teamScheduleDb.events)
		} else {
			null
		}
	}

	@Throws(IOException::class)
	fun getFullSchedule(currentDataVersion: Long): Schedule? {
		val scheduleResponseBody: Schedule? = nbaScheduleRepository.findSchedule().firstOrNull()?.run {
			Schedule(dataVersion, events)
		}
		return when {
			scheduleResponseBody == null -> {
				throw NoContent
			}
			scheduleResponseBody.dataVersion > currentDataVersion -> {
				scheduleResponseBody
			}
			else -> {
				null
			}
		}
	}

	@Throws(IOException::class)
	fun getStanding(currentDataVersion: Long): Standing? {
		val standingDataDb: NbaStandingEntity? = nbaStandingRepository.findLatestStandings()?.firstOrNull()
		return when {
			standingDataDb == null -> {
				throw NoContent
			}
			standingDataDb.dataVersion > currentDataVersion -> {
				Standing(
					dataVersion = standingDataDb.dataVersion,
					western = standingDataDb.western,
					eastern = standingDataDb.eastern
				)
			}
			else -> {
				null
			}
		}
	}

	@Throws(IOException::class)
	fun getTransactions(currentDataVersion: Long): NbaTransactionsEntity? {
		val transactions: NbaTransactionsEntity? = nbaTransactionsRepository.findTransactions()
		return when {
			transactions == null -> {
				throw NoContent
			}
			transactions.dataVersion > currentDataVersion -> transactions
			else -> {
				null
			}
		}
	}
}