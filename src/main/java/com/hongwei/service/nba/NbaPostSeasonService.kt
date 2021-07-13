package com.hongwei.service.nba

import com.hongwei.constants.NoContent
import com.hongwei.constants.ResetContent
import com.hongwei.model.jpa.nba.NbaPlayInRepository
import com.hongwei.model.jpa.nba.NbaPostSeasonRepository
import com.hongwei.model.nba.EventType
import com.hongwei.model.nba.PostSeason
import com.hongwei.model.nba.mapper.PostSeasonMapper
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class NbaPostSeasonService {
    private val logger: Logger = LogManager.getLogger(NbaPostSeasonService::class.java)

    @Autowired
    private lateinit var nbaAnalysisService: NbaAnalysisService

    @Autowired
    private lateinit var nbaPlayInRepository: NbaPlayInRepository

    @Autowired
    private lateinit var nbaPostSeasonRepository: NbaPostSeasonRepository

    @Throws(IOException::class)
    fun getSeasonStatus(): EventType? = nbaAnalysisService.doAnalysisSeasonStatus()

    @Throws(IOException::class)
    fun getPostSeason(dataVersion: Long): PostSeason? {
        val stage = nbaAnalysisService.doAnalysisSeasonStatus() ?: throw NoContent
        val entity = when {
            EventType.isPlayInOngoing(stage) -> PostSeasonMapper.mapPlayIn(stage, nbaPlayInRepository.findPlayIn().firstOrNull())
            EventType.isPlayOffStarted(stage) -> PostSeasonMapper.map(stage, nbaPostSeasonRepository.findPostSeason().firstOrNull())
            else -> throw NoContent
        }
        if (dataVersion >= entity?.dataVersion ?: 0) {
            throw ResetContent
        }
        return entity
    }
}