package com.hongwei.controller

import com.google.gson.Gson
import com.hongwei.constants.BadRequest
import com.hongwei.constants.ResetContent
import com.hongwei.model.jpa.nba.NbaTeamDetailEntity
import com.hongwei.model.jpa.nba.NbaTeamScheduleEntity
import com.hongwei.model.nba.*
import com.hongwei.model.nba.espn.TeamDetailSource
import com.hongwei.model.nba.espn.TeamScheduleSource
import com.hongwei.model.nba.espn.mapper.TeamDetailMapper
import com.hongwei.model.nba.espn.mapper.TeamScheduleMapper
import com.hongwei.service.nba.*
import com.hongwei.service.nba.EspnCurlService.Companion.TEAMS
import com.hongwei.util.TimeStampUtil
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/hub")
class NbaHubController {
    private val logger: Logger = LogManager.getLogger(NbaHubController::class.java)

    @Autowired
    private lateinit var statCurlService: EspnCurlService

    @Autowired
    private lateinit var dbWriterService: DbWriterService

    @Autowired
    private lateinit var nbaAnalysisService: NbaAnalysisService

    @Autowired
    private lateinit var espnStandingParseService: EspnStandingParseService

    @Autowired
    private lateinit var nbaDetailService: NbaDetailService

    @GetMapping(path = ["/test.do"])
    @ResponseBody
    fun test(url: String): ResponseEntity<*> = statCurlService.curl(url)?.let {
        ResponseEntity.ok(it.toString())
    } ?: throw BadRequest

    @GetMapping(path = ["/espnTeams.do"])
    @ResponseBody
    fun getEspnTeamShorts(): ResponseEntity<*> = ResponseEntity.ok(statCurlService.getTeamList())

    @GetMapping(path = ["/espnTeamSchedule.do"])
    @ResponseBody
    fun getEspnTeamSchedule(team: String): ResponseEntity<*> =
            statCurlService.getTeamScheduleJson(statCurlService.getTeamScheduleCurlDoc(team))?.let {
                ResponseEntity.ok(it)
            } ?: throw ResetContent

    @PutMapping(path = ["/espnAllTeamsSchedule.do"])
    @ResponseBody
    fun generateEspnAllTeamSchedule(): ResponseEntity<*> {
        val eventSet = mutableSetOf<Event>()
        TEAMS.forEach { team ->
            mergeIntoList(eventSet, generateScheduleForEachTeam(team))
        }
        eventSet.sortedByDescending { it.unixTimeStamp }
        val dataVersion = TimeStampUtil.getTimeVersionWithMinute()
        dbWriterService.writeFullSchedule(dataVersion, eventSet.toList())
        return ResponseEntity.ok(null)
    }

    @PutMapping(path = ["/espnTeamSchedule.do"])
    @ResponseBody
    fun generateEspnTeamSchedule(team: String): ResponseEntity<*> {
        generateScheduleForEachTeam(team)
        return ResponseEntity.ok(null)
    }

    @GetMapping(path = ["/nbaStage.do"])
    @ResponseBody
    fun getNbaStage(): ResponseEntity<*> =
            ResponseEntity.ok(nbaAnalysisService.doAnalysisSeasonStatus())

    @GetMapping(path = ["/nbaAnalysis.do"])
    @ResponseBody
    fun doNbaAnalysis(): ResponseEntity<*> =
            ResponseEntity.ok(nbaAnalysisService.doAnalysisPostSeason())

    @GetMapping(path = ["/espnStanding.do"])
    @ResponseBody
    fun getEspnStanding(): ResponseEntity<*> {
        val standingData = statCurlService.getStanding()?.let { standingHtmlDocument ->
            espnStandingParseService.parseStanding(standingHtmlDocument)
        }
        return ResponseEntity.ok(Gson().toJson(standingData))
    }

    @PutMapping(path = ["/espnStanding.do"])
    @ResponseBody
    fun generateEspnStandingDb(): ResponseEntity<*> {
        statCurlService.getStanding()?.let { standingHtmlDocument ->
            val standingData = espnStandingParseService.parseStanding(standingHtmlDocument)
            standingData?.let {
                val teamDetailDb = nbaDetailService.getAllTeamDetail()
                if (teamDetailDb.isNotEmpty()) {
                    dbWriterService.writeStanding(teamDetailDb, it)
                } else {
                    logger.info("generateEspnStandingDb, no team details found, you may need to generate teamSchedule first.")
                }
            }
        }
        return ResponseEntity.ok(null)
    }

    @PutMapping(path = ["/espnTransactions.do"])
    @ResponseBody
    fun generateEspnTransactions(): ResponseEntity<*> =
            nbaAnalysisService.saveTransactions()?.let {
                ResponseEntity.ok(it)
            } ?: throw ResetContent

    private fun generateScheduleForEachTeam(team: String): List<Event> {
        val curlDoc = statCurlService.getTeamScheduleCurlDoc(team)
        val teamDetailEntity: NbaTeamDetailEntity = TeamDetailMapper.map(
                Gson().fromJson(statCurlService.getTeamDetailJson(curlDoc), TeamDetailSource::class.java)
        )
        val teamScheduleSourceObj = statCurlService.getTeamScheduleJson(curlDoc)

        val teamScheduleEntity: NbaTeamScheduleEntity = teamScheduleSourceObj?.let {
            TeamScheduleMapper.map(team, Gson().fromJson(teamScheduleSourceObj, TeamScheduleSource::class.java))
        } ?: NbaTeamScheduleEntity
                .emptyEntity(team, TimeStampUtil.getTimeVersionWithMinute())
        dbWriterService.writeTeamDetail(teamDetailEntity)
        dbWriterService.writeTeamSchedule(teamScheduleEntity)
        return teamScheduleEntity.events.map { TeamScheduleMapper.teamEventMapToEvent(it, teamDetailEntity) }
    }

    private fun mergeIntoList(eventSet: MutableSet<Event>, adding: List<Event>) {
        eventSet.addAll(adding)
    }
}