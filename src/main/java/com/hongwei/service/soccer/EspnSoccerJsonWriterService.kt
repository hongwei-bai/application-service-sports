package com.hongwei.service.soccer

import com.hongwei.constants.AppDataConfigurations
import com.hongwei.constants.Constants
import com.hongwei.util.JsonWriterUtil.writeJsonString
import com.hongwei.util.TimeStampUtil
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class EspnSoccerJsonWriterService {
    private val logger: Logger = LogManager.getLogger(EspnSoccerJsonWriterService::class.java)

    @Autowired
    private lateinit var appDataConfigurations: AppDataConfigurations

    fun writeStanding(jsonString: String) {
        writeJsonString(jsonString,
                "${appDataConfigurations.dataPath}${Constants.AppDataPath.SOCCER_DATA_PATH}/standing.json")
        writeJsonString(jsonString,
                "${appDataConfigurations.dataPath}${Constants.AppDataPath.SOCCER_DATA_PATH}/standing_${TimeStampUtil.getTimeVersionWithHour()}.json")
    }

    fun writeTeamSchedule(team: String, jsonString: String) {
        writeJsonString(jsonString,
                "${appDataConfigurations.dataPath}${Constants.AppDataPath.SOCCER_DATA_PATH}/schedule_$team.json")
    }
}