package com.hongwei.service.nba

import com.hongwei.constants.AppDataConfigurations
import com.hongwei.constants.Constants
import com.hongwei.model.nba.espn.StandingSource
import com.hongwei.util.JsonWriterUtil.writeJSONObject
import com.hongwei.util.JsonWriterUtil.writeJsonString
import com.hongwei.util.TimeStampUtil
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class JsonWriterService {
    private val logger: Logger = LogManager.getLogger(JsonWriterService::class.java)

    @Autowired
    private lateinit var appDataConfigurations: AppDataConfigurations

    fun writeStanding(jsonObj: StandingSource) {
        writeJSONObject(jsonObj, "${appDataConfigurations.dataPath}${Constants.AppDataPath.NBA_DATA_PATH}/standing.json")
        writeJSONObject(jsonObj, "${appDataConfigurations.dataPath}${Constants.AppDataPath.NBA_DATA_PATH}/standing_${TimeStampUtil.getTimeVersionWithHour()}.json")
    }

    fun writeTeamSchedule(team: String, jsonString: String) {
        writeJsonString(jsonString, "${appDataConfigurations.dataPath}${Constants.AppDataPath.NBA_DATA_PATH}/schedule_$team.json")
    }
}