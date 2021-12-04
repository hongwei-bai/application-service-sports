package com.hongwei.service.nba

import com.hongwei.constants.AppDataConfigurations
import com.hongwei.constants.Constants.LogoPath.NBA
import com.hongwei.model.jpa.nba.NbaTeamDetailEntity
import com.hongwei.model.jpa.nba.NbaTeamDetailRepository
import com.hongwei.model.nba.TeamDetail
import com.hongwei.model.nba.mapper.TeamDetailMapper
import com.hongwei.util.FileNameUtils
import com.hongwei.util.WebImageDownloadUtil
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class NbaDetailService {
    private val logger: Logger = LogManager.getLogger(NbaDetailService::class.java)

    @Autowired
    private lateinit var appDataConfigurations: AppDataConfigurations

    @Autowired
    private lateinit var nbaTeamDetailRepository: NbaTeamDetailRepository

    @Throws(IOException::class)
    fun getTeamDetail(team: String): TeamDetail? {
        val entity = nbaTeamDetailRepository.findTeamDetail(team)
        return entity?.let {
            entity.logo = getAppLogoUrl(it.logo)
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

    fun downloadNbaTeamLogo(logoUrl: String): String {
        val imageFileName = FileNameUtils.getFileName(logoUrl)
        val destPath = "${appDataConfigurations.imagePath}$NBA$imageFileName"
        val downloadSuccess = WebImageDownloadUtil.downloadWebImage(logoUrl, destPath)
        if (!downloadSuccess) {
            logger.warn("downloadNbaTeamLogo failed, logoUrl: $logoUrl -> dest: $destPath")
        }
        return "${appDataConfigurations.imagePathUrl}$NBA$imageFileName"
    }

    fun getAppLogoUrl(logoUrl: String): String =
            "${appDataConfigurations.imagePathUrl}$NBA${FileNameUtils.getFileName(logoUrl)}"
}