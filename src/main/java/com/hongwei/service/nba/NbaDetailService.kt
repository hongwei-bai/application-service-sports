package com.hongwei.service.nba

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.hongwei.constants.Constants
import com.hongwei.constants.Constants.AppDataPath.NBA_DATA_PATH
import com.hongwei.model.jpa.nba.NbaTeamDetailEntity
import com.hongwei.model.jpa.nba.NbaTeamDetailRepository
import com.hongwei.model.nba.TeamDetail
import com.hongwei.model.nba.mapper.TeamDetailMapper
import com.hongwei.model.nba.theme_v1.NbaTeamTheme
import com.hongwei.model.nba.theme_v1.NbaTeamThemeJson
import com.hongwei.model.nba.theme_v1.NbaTeamThemeMapper
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException

@Service
class NbaDetailService {
    private val logger: Logger = LogManager.getLogger(NbaDetailService::class.java)

    @Value("\${appdata.dataPath}")
    private lateinit var appDataPath: String

    @Autowired
    private lateinit var nbaTeamDetailRepository: NbaTeamDetailRepository

    @Deprecated("NBA_V1 obsoleted api. V2 use getTeamDetail instead.")
    @Throws(IOException::class)
    fun getTeamTheme(team: String, currentDataVersion: Long): NbaTeamTheme? {
        val jsonPath = "$appDataPath$NBA_DATA_PATH${Constants.AppDataPath.TEAM_THEME_JSON_PATH}"
        val teamTheme = ObjectMapper().registerModule(KotlinModule())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue(File(jsonPath), NbaTeamThemeJson::class.java)
        return NbaTeamThemeMapper.map(teamTheme, team)
    }

    @Throws(IOException::class)
    fun getTeamDetail(team: String): TeamDetail? {
        val entity = nbaTeamDetailRepository.findTeamDetail(team)
        return entity?.let {
            TeamDetailMapper.map(entity)
        }
    }

    @Throws(IOException::class)
    fun getAllTeamDetail(): Map<String, NbaTeamDetailEntity> {
        val list = nbaTeamDetailRepository.findAllTeamDetail()
        return list.map {
            it.team to it
        }.toMap()
    }
}