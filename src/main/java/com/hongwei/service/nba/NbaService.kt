package com.hongwei.service.nba

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.hongwei.constants.Constants.AppDataPath.STANDING_JSON_PATH
import com.hongwei.constants.Constants.AppDataPath.TEAM_SCHEDULE_JSON_PATH
import com.hongwei.model.nba.*
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
    fun getScheduleByTeam(team: String, currentDataVersion: Long): TeamSchedule? {
        val jsonPath = "$nbaAppDataPath$TEAM_SCHEDULE_JSON_PATH".replace("{team}", team)
        val teamSchedule = ObjectMapper().registerModule(KotlinModule())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue(File(jsonPath), TeamScheduleSource::class.java)
        return if ((teamSchedule.dataVersion ?: 0) > currentDataVersion) {
            TeamScheduleMapper.map(teamSchedule)
        } else {
            null
        }
    }

    @Throws(IOException::class)
    fun getStanding(currentDataVersion: Long): StandingData? {
        val jsonPath = "$nbaAppDataPath$STANDING_JSON_PATH"
        val standingData = ObjectMapper().registerModule(KotlinModule())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue(File(jsonPath), StandingSource::class.java)
        return if ((standingData.dataVersion ?: 0) > currentDataVersion) {
            StandingMapper.map(standingData)
        } else {
            null
        }
    }
}