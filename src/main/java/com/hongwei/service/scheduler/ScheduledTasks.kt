package com.hongwei.service.scheduler

import com.hongwei.constants.Constants.TimeZone.SYDNEY
import com.hongwei.controller.NbaHubController
import com.hongwei.model.nba.EventType
import com.hongwei.service.nba.NbaAnalysisService
import com.hongwei.service.soccer.SoccerAnalysisService
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*

@Component
class ScheduledTasks {
    private val logger: Logger = LogManager.getLogger(ScheduledTasks::class.java)

    @Autowired
    private lateinit var nbaHubController: NbaHubController

    @Autowired
    private lateinit var nbaAnalysisService: NbaAnalysisService

    @Autowired
    private lateinit var soccerAnalysisService: SoccerAnalysisService

    private var initialized = false

    // 60 mins : 60 min x 60 s x 1000 ms = 1,800,000, For copy:3600000
    @Scheduled(fixedRate = 3600000)
    fun reportCurrentTime() {
        val sydTime = Calendar.getInstance(TimeZone.getTimeZone(SYDNEY))
        val hour = sydTime.get(Calendar.HOUR_OF_DAY)

        runBlocking {
            if (SoccerHoursUpdate.contains(hour)) {
                if (!initialized) {
                    initializeOnce()
                } else {
                    soccerAnalysisService.getQueryingLeagues().forEach { league ->
                        soccerAnalysisService.fetchStandings(league)
                    }
                }
            }
            delay(1000 * 300)
            soccerAnalysisService.getQueryingTeams().forEach { teamDetail ->
                soccerAnalysisService.fetchTeamSchedules(teamDetail.id)
            }

            if (NBAHoursUpdate.contains(hour)) {
                when (nbaAnalysisService.doAnalysisSeasonStatus()) {
                    EventType.PreSeason,
                    EventType.Season -> {
                        nbaHubController.generateEspnStandingDb()
                    }
                    else -> null
                }


                delay(1000 * 30)
                nbaHubController.generateEspnAllTeamSchedule()

                delay(1000 * 300)
                when (nbaAnalysisService.doAnalysisSeasonStatus()) {
                    EventType.PlayIn,
                    EventType.PlayOffRound1,
                    EventType.PlayOffRound2,
                    EventType.PlayOffConferenceFinal,
                    EventType.PlayOffGrandFinal -> {
                        nbaAnalysisService.doAnalysisPostSeason()
                    }
                    else -> null
                }

                delay(1000 * 300)
                nbaAnalysisService.saveTransactions()
            }
        }
    }

    private fun initializeOnce() {
        initialized = true
        soccerAnalysisService.initializeLeagues()
    }

    companion object {
        val NBAHoursUpdate = listOf(4, 16)

        val SoccerHoursUpdate = listOf(7, 20)
    }
}