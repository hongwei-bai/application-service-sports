package com.hongwei.service.nba

import com.hongwei.constants.NoContent
import com.hongwei.model.jpa.NbaStandingEntity
import com.hongwei.model.jpa.NbaStandingRepository
import com.hongwei.model.jpa.NbaTeamScheduleEntity
import com.hongwei.model.jpa.NbaTeamScheduleRepository
import com.hongwei.model.nba.ConferenceStandingData
import com.hongwei.model.nba.StandingData
import com.hongwei.model.nba.TeamSchedule
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class NbaService {
    private val logger: Logger = LogManager.getLogger(NbaService::class.java)

    @Value("\${appdata.dataPath}")
    private lateinit var appDataPath: String

    @Autowired
    private lateinit var nbaTeamScheduleRepository: NbaTeamScheduleRepository

    @Autowired
    private lateinit var nbaStandingRepository: NbaStandingRepository

    @Throws(IOException::class)
    fun getScheduleByTeam(team: String, currentDataVersion: Long): TeamSchedule? {
        val teamScheduleDb: NbaTeamScheduleEntity? = nbaTeamScheduleRepository.findScheduleByTeam(team)
        return when {
            teamScheduleDb?.events == null -> {
                throw NoContent
            }
            (teamScheduleDb.dataVersion ?: 0) > currentDataVersion -> {
                TeamSchedule(teamScheduleDb.dataVersion ?: 0, teamScheduleDb.teamDetail!!, teamScheduleDb.events!!)
            }
            else -> {
                null
            }
        }
    }

    @Throws(IOException::class)
    fun getStanding(currentDataVersion: Long): StandingData? {
        val standingDataDb: NbaStandingEntity? = nbaStandingRepository.findLatestStandings()?.firstOrNull()
        return if (standingDataDb?.easternStandings == null || standingDataDb.westernStandings == null) {
            throw NoContent
        } else if ((standingDataDb.dataVersion ?: 0) > currentDataVersion) {
            StandingData(
                    dataVersion = standingDataDb.dataVersion ?: 0,
                    western = ConferenceStandingData(standingDataDb.westernStandings!!),
                    eastern = ConferenceStandingData(standingDataDb.easternStandings!!)
            )
        } else {
            null
        }
    }
}