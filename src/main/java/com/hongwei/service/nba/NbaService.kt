package com.hongwei.service.nba

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.hongwei.constants.Constants.AppDataPath.TEAM_SCHEDULE_JSON_PATH
import com.hongwei.model.nba.NbaScheduleResponse
import com.hongwei.model.nba.TeamScheduleMapper
import com.hongwei.model.nba.TeamScheduleSource
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException

@Service
class NbaService {
    private val logger: Logger = LogManager.getLogger(NbaService::class.java)

    @Value("\${appdata.nbaPath}")
    private lateinit var nbaAppDataPath: String

    @Throws(IOException::class)
    fun getScheduleByTeam(team: String, currentDataVersion: Long): NbaScheduleResponse? {
        val jsonPath = "$nbaAppDataPath$TEAM_SCHEDULE_JSON_PATH".replace("{team}", team)
        val teamSchedule = ObjectMapper().registerModule(KotlinModule())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue(File(jsonPath), TeamScheduleSource::class.java)
        return if ((teamSchedule.dataVersion ?: 0) > currentDataVersion) {
            NbaScheduleResponse(TeamScheduleMapper.map(teamSchedule))
        } else {
            null
        }
    }
}