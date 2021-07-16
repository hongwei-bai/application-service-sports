package com.hongwei.service.soccer

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.hongwei.constants.Constants
import com.hongwei.model.nba.theme.NbaTeamTheme
import com.hongwei.model.nba.theme.NbaTeamThemeJson
import com.hongwei.model.nba.theme.NbaTeamThemeMapper
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException

@Service
class SoccerConfigurationService {
    private val logger: Logger = LogManager.getLogger(SoccerConfigurationService::class.java)

    @Value("\${appdata.dataPath}")
    private lateinit var appDataPath: String

    @Throws(IOException::class)
    fun getTeamTheme(team: String, currentDataVersion: Long): NbaTeamTheme? {
        val jsonPath = "$appDataPath${Constants.AppDataPath.SOCCER_DATA_PATH}${Constants.AppDataPath.SOCCER_LEAGUE_JSON_PATH}"
        val teamTheme = ObjectMapper().registerModule(KotlinModule())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue(File(jsonPath), NbaTeamThemeJson::class.java)
        return NbaTeamThemeMapper.map(teamTheme, team)
    }


}