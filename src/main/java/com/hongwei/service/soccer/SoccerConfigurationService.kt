package com.hongwei.service.soccer

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.hongwei.constants.AppDataConfigurations
import com.hongwei.constants.Constants
import com.hongwei.constants.Constants.LogoPath.LOGO_PLACEHOLDER
import com.hongwei.constants.Constants.LogoPath.SOCCER
import com.hongwei.model.nba.theme_v1.NbaTeamTheme
import com.hongwei.model.nba.theme_v1.NbaTeamThemeJson
import com.hongwei.model.nba.theme_v1.NbaTeamThemeMapper
import com.hongwei.util.FileNameUtils
import com.hongwei.util.WebImageDownloadUtil
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException

@Service
class SoccerConfigurationService {
    private val logger: Logger = LogManager.getLogger(SoccerConfigurationService::class.java)

    @Autowired
    private lateinit var appDataConfigurations: AppDataConfigurations

    @Throws(IOException::class)
    fun getTeamTheme(team: String, currentDataVersion: Long): NbaTeamTheme? {
        val jsonPath = "${appDataConfigurations.dataPath}${Constants.AppDataPath.SOCCER_DATA_PATH}${Constants.AppDataPath.SOCCER_LEAGUE_JSON_PATH}"
        val teamTheme = ObjectMapper().registerModule(KotlinModule())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue(File(jsonPath), NbaTeamThemeJson::class.java)
        return NbaTeamThemeMapper.map(teamTheme, team)
    }

    fun downloadSoccerTeamLogo(logoUrl: String?): String? {
        if (logoUrl == null) {
            return logoUrl
        }
        val imageFileName = FileNameUtils.getFileName(logoUrl)
        val destPath = "${appDataConfigurations.imagePath}$SOCCER$imageFileName"
        val downloadSuccess = WebImageDownloadUtil.downloadWebImage(logoUrl, destPath)
        if (!downloadSuccess) {
            logger.warn("downloadSoccerTeamLogo failed, logoUrl: $logoUrl -> dest: $destPath")
        }
        return "${Constants.LogoPath.URL_BASE}$SOCCER$imageFileName"
    }

    fun getAppLogoUrl(logoUrl: String?): String =
            logoUrl?.let {
                "${Constants.LogoPath.URL_BASE}$SOCCER${FileNameUtils.getFileName(logoUrl)}"
            } ?: "${Constants.LogoPath.URL_BASE}$LOGO_PLACEHOLDER"
}