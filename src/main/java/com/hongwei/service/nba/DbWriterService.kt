package com.hongwei.service.nba

import com.google.gson.Gson
import com.hongwei.model.jpa.NbaStandingEntity
import com.hongwei.model.jpa.NbaStandingRepository
import com.hongwei.model.jpa.NbaTeamScheduleEntity
import com.hongwei.model.jpa.NbaTeamScheduleRepository
import com.hongwei.model.nba.*
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DbWriterService {
    private val logger: Logger = LogManager.getLogger(DbWriterService::class.java)

    @Autowired
    private lateinit var nbaTeamScheduleRepository: NbaTeamScheduleRepository

    @Autowired
    private lateinit var nbaStandingRepository: NbaStandingRepository

    fun writeStanding(jsonObj: StandingSource) {
        val data: StandingData = StandingMapper.map(jsonObj)
        val lastStandingRecordInDb: NbaStandingEntity? = nbaStandingRepository.findLatestStandings()?.firstOrNull()
        if (data.dataVersion != lastStandingRecordInDb?.dataVersion) {
            val entity = NbaStandingEntity(
                    dataVersion = data.dataVersion,
                    easternStandings = data.eastern.teams,
                    westernStandings = data.western.teams
            )
            nbaStandingRepository.save(entity)
        }
    }

    fun writeTeamSchedule(teamDetailJsonString: String, teamScheduleJsonString: String) {
        logger.debug("teamDetailJsonString: $teamDetailJsonString")
        val teamDetailSourceObj: TeamDetailSource = Gson().fromJson(teamDetailJsonString, TeamDetailSource::class.java)
        val teamScheduleSourceObj: TeamScheduleSource = Gson().fromJson(teamScheduleJsonString, TeamScheduleSource::class.java)
        val data = TeamScheduleMapper.map(teamDetailSourceObj, teamScheduleSourceObj)
        val entity = NbaTeamScheduleEntity(data.teamDetail.abbrev, data.dataVersion, data.teamDetail, data.events)
        nbaTeamScheduleRepository.save(entity)
    }
}