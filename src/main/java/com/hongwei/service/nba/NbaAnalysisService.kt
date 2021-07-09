package com.hongwei.service.nba

import com.hongwei.model.jpa.*
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class NbaAnalysisService {
    private val logger: Logger = LogManager.getLogger(NbaAnalysisService::class.java)

    @Autowired
    private lateinit var nbaTeamScheduleRepository: NbaTeamScheduleRepository

    @Autowired
    private lateinit var nbaTeamDetailRepository: NbaTeamDetailRepository

    @Autowired
    private lateinit var nbaStandingRepository: NbaStandingRepository

    @Autowired
    private lateinit var nbaScheduleRepository: NbaScheduleRepository

    @Throws(IOException::class)
    fun doAnalysisSeasonStatus() {
        val standingDataDb: NbaStandingEntity = nbaStandingRepository.findLatestStandings()?.firstOrNull() ?: return
        val playOffTeams = listOf(
                standingDataDb.western.filter { it.rank <= 6 }.map { it.teamAbbr },
                standingDataDb.eastern.filter { it.rank <= 6 }.map { it.teamAbbr }
        ).flatten()
        val playInTeams = listOf(
                standingDataDb.western.filter { it.rank == 7 || it.rank == 8 }.map { it.teamAbbr },
                standingDataDb.eastern.filter { it.rank == 7 || it.rank == 8 }.map { it.teamAbbr }
        ).flatten()
        val tails = listOf(standingDataDb.western.last().teamAbbr, standingDataDb.eastern.last().teamAbbr)


    }
}