package com.hongwei.service.nba

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.hongwei.constants.Constants.AppDataPath.NBA_DATA_PATH
import com.hongwei.constants.Constants.AppDataPath.PLAYOFF_SOURCE_JSON_PATH
import com.hongwei.model.nba.PlayOffResponseBody
import com.hongwei.model.nba.espn.PlayOffSourceRoot
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException

@Service
class NbaPlayOffService {
    private val logger: Logger = LogManager.getLogger(NbaPlayOffService::class.java)

    @Value("\${appdata.dataPath}")
    private lateinit var appDataPath: String

    @Throws(IOException::class)
    fun getPlayOff(currentDataVersion: Long): PlayOffResponseBody? {
        val jsonPath = "$appDataPath$NBA_DATA_PATH$PLAYOFF_SOURCE_JSON_PATH"
        val playOffSourceData = ObjectMapper().registerModule(KotlinModule())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue(File(jsonPath), PlayOffSourceRoot::class.java)
        return if (playOffSourceData.dataVersion > currentDataVersion) {
            playOffSourceData.map()
        } else {
            null
        }
    }

    fun isSeasonOngoing(): Boolean =
            try {
                val jsonPath = "$appDataPath$NBA_DATA_PATH$PLAYOFF_SOURCE_JSON_PATH"
                val playOffSourceData = ObjectMapper().registerModule(KotlinModule())
                        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                        .readValue(File(jsonPath), PlayOffSourceRoot::class.java)
                playOffSourceData.seasonOngoing
            } catch (e: Exception) {
                true
            }
}