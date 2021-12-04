package com.hongwei.service.scheduler

import com.hongwei.constants.Constants.TimeZone.SYDNEY
import com.hongwei.controller.NbaHubController
import com.hongwei.model.nba.EventType
import com.hongwei.service.nba.NbaAnalysisService
import com.hongwei.service.soccer.SoccerAnalysisService
import kotlinx.coroutines.*
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.Executors

@Component
class ScheduledTasks {
    private val logger: Logger = LogManager.getLogger(ScheduledTasks::class.java)

    @Autowired
    private lateinit var nbaHubController: NbaHubController

    @Autowired
    private lateinit var nbaAnalysisService: NbaAnalysisService

    @Autowired
    private lateinit var soccerAnalysisService: SoccerAnalysisService

    private val dispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()

    private var initialized = false

    // 60 mins : 60 min x 60 s x 1000 ms = 1,800,000, For copy:3600000
    @Scheduled(fixedRate = 3600000)
    fun reportCurrentTime() {
        val sydTime = Calendar.getInstance(TimeZone.getTimeZone(SYDNEY))
        val dayOfMonth = sydTime.get(Calendar.DAY_OF_MONTH)
        val hour = sydTime.get(Calendar.HOUR_OF_DAY)

        logger.debug("scheduler - reportCurrentTime, hour: $hour")
        val downloadLogos = !initialized || logoUpdateDayOfMonth.contains(dayOfMonth)
        logger.debug("scheduler - downloadLogos flag: $downloadLogos")

        runBlocking(dispatcher) {
            val jobSoccer = async {
                logger.debug("scheduler - runBlocking, contained in soccer hours: ${SoccerHoursUpdate.contains(hour)}")
                if (SoccerHoursUpdate.contains(hour)) {
                    if (!initialized) {
                        logger.info("scheduler - initializeOnce for soccer")
                        initializeOnce()
                    } else {
                        logger.info("scheduler - soccer getQueryingLeagues")
                        soccerAnalysisService.getQueryingLeagues().forEach { league ->
                            soccerAnalysisService.fetchStandings(league)
                        }
                    }
                    delay(1000 * 10)
                    val teams = soccerAnalysisService.getQueryingTeams()
                    logger.debug("scheduler - soccer start fetchTeamSchedules...")
                    teams.forEach { teamDetail ->
                        soccerAnalysisService.fetchTeamSchedules(teamDetail.id)
                    }
                    logger.info("scheduler - soccer finish fetchTeamSchedules - ${teams.size} teams")
                }
            }

            val jobNba = async {
                delay(1000 * 30)
                logger.debug("scheduler - runBlocking, contained in NBA hours: ${NBAHoursUpdate.contains(hour)}")
                if (NBAHoursUpdate.contains(hour)) {
                    logger.info("schedule for NBA, available hours")
                    val seasonStatus = nbaAnalysisService.doAnalysisSeasonStatus()
                    logger.debug("schedule for NBA, seasonStatus: $seasonStatus")
                    when (seasonStatus) {
                        EventType.PreSeason,
                        EventType.Season -> {
                            logger.debug("schedule for NBA, start generateEspnStandingDb...")
                            nbaHubController.generateEspnStandingDb()
                        }
                        else -> null
                    }

                    delay(1000 * 30)
                    logger.debug("schedule for NBA, start generateEspnAllTeamSchedule...")
                    nbaHubController.generateEspnAllTeamSchedule(downloadLogos)
                    logger.debug("schedule for NBA, finish generateEspnAllTeamSchedule")

                    delay(1000 * 30)
                    when (seasonStatus) {
                        EventType.PlayIn,
                        EventType.PlayOffRound1,
                        EventType.PlayOffRound2,
                        EventType.PlayOffConferenceFinal,
                        EventType.PlayOffGrandFinal -> {
                            logger.debug("schedule for NBA, start doAnalysisPostSeason...")
                            nbaAnalysisService.doAnalysisPostSeason()
                            logger.debug("schedule for NBA, finish doAnalysisPostSeason")
                        }
                        else -> null
                    }

                    delay(1000 * 30)
                    logger.debug("schedule for NBA, start saveTransactions...")
                    nbaAnalysisService.saveTransactions()
                    logger.info("schedule for NBA, finish saveTransactions")
                }
            }

            jobSoccer.start()
            jobNba.start()
        }
    }

    private fun initializeOnce() {
        initialized = true
        soccerAnalysisService.initializeLeagues()
    }

    companion object {
        val NBAHoursUpdate = listOf(4, 12, 16)

        val SoccerHoursUpdate = listOf(7, 20)

        val logoUpdateDayOfMonth = listOf(1)
    }
}