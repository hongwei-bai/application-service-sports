package com.hongwei.service.nba

import com.hongwei.model.jpa.nba.*
import com.hongwei.model.nba.Event
import com.hongwei.model.nba.Standing
import com.hongwei.model.nba.TeamDetail
import com.hongwei.model.nba.espn.mapper.StandingMapper
import com.hongwei.model.nba.espn.StandingSource
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

    @Autowired
    private lateinit var nbaTeamDetailRepository: NbaTeamDetailRepository

    @Autowired
    private lateinit var nbaScheduleRepository: NbaScheduleRepository

    fun writeStanding(teamDetailMap: Map<String, NbaTeamDetailEntity>, jsonObj: StandingSource) {
        val data: Standing = StandingMapper.map(teamDetailMap, jsonObj)
        val lastStandingRecordInDb: NbaStandingEntity? = nbaStandingRepository.findLatestStandings()?.firstOrNull()
        if (data.dataVersion != lastStandingRecordInDb?.dataVersion) {
            val entity = NbaStandingEntity(
                    dataVersion = data.dataVersion,
                    eastern = data.eastern,
                    western = data.western
            )
            nbaStandingRepository.save(entity)
        }
    }

    fun writeTeamSchedule(teamScheduleEntity: NbaTeamScheduleEntity) {
        nbaTeamScheduleRepository.save(teamScheduleEntity)
    }

    fun writeTeamDetail(teamDetailEntity: NbaTeamDetailEntity) {
        nbaTeamDetailRepository.save(teamDetailEntity)
    }

    fun writeFullSchedule(dataVersion: Long, events: List<Event>) {
        nbaScheduleRepository.save(NbaScheduleEntity(dataVersion = dataVersion, events = events))
    }
}