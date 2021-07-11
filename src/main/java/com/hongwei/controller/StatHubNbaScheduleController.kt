package com.hongwei.controller

import com.google.gson.Gson
import com.hongwei.constants.BadRequest
import com.hongwei.constants.ResetContent
import com.hongwei.model.jpa.NbaTeamDetailEntity
import com.hongwei.model.jpa.NbaTeamScheduleEntity
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
class StatHubNbaScheduleController {
    private val logger: Logger = LogManager.getLogger(StatHubNbaScheduleController::class.java)

    @Autowired
    private lateinit var statCurlService: EspnCurlService

    @Autowired
    private lateinit var dbWriterService: DbWriterService

    @Autowired
    private lateinit var nbaService: NbaService

    @Autowired
    private lateinit var nbaPlayOffService: NbaPlayOffService

    @Autowired
    private lateinit var nbaAnalysisService: NbaAnalysisService

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
    fun getEspnTeamSchedule(team: String, dataVersionBase: Int? = null): ResponseEntity<*> =
            statCurlService.getTeamScheduleJson(statCurlService.getTeamScheduleCurlDoc(team), dataVersionBase
                    ?: 0)?.let {
                ResponseEntity.ok(it)
            } ?: throw ResetContent

    @PutMapping(path = ["/espnAllTeamsSchedule.do"])
    @ResponseBody
    fun generateEspnAllTeamSchedule(dataVersionBase: Int? = null): ResponseEntity<*> {
        val eventSet = mutableSetOf<Event>()
        TEAMS.forEach { team ->
            mergeIntoList(eventSet, generateScheduleForEachTeam(team, dataVersionBase))
        }
        eventSet.sortedByDescending { it.unixTimeStamp }
        val dataVersion = TimeStampUtil.getTimeVersionWithDayAndDataVersion(dataVersion = dataVersionBase)
        dbWriterService.writeFullSchedule(dataVersion, eventSet.toList())
        return ResponseEntity.ok(null)
    }

    @PutMapping(path = ["/espnTeamSchedule.do"])
    @ResponseBody
    fun generateEspnTeamSchedule(team: String, dataVersionBase: Int? = null): ResponseEntity<*> {
        generateScheduleForEachTeam(team, dataVersionBase)
        return ResponseEntity.ok(null)
    }

    @GetMapping(path = ["/nbaStage.do"])
    @ResponseBody
    fun getNbaStage(): ResponseEntity<*> =
            ResponseEntity.ok(nbaAnalysisService.doAnalysisSeasonStatus())

    @GetMapping(path = ["/nbaAnalysis.do"])
    @ResponseBody
    fun doNbaAnalysis(): ResponseEntity<*> =
            ResponseEntity.ok(nbaAnalysisService.doAnalysis())

    private fun generateScheduleForEachTeam(team: String, dataVersionBase: Int?): List<Event> {
        val curlDoc = statCurlService.getTeamScheduleCurlDoc(team)
        val teamDetailEntity: NbaTeamDetailEntity = TeamDetailMapper.map(
                Gson().fromJson(statCurlService.getTeamDetailJson(curlDoc), TeamDetailSource::class.java)
        )
        val teamScheduleSourceObj = statCurlService.getTeamScheduleJson(curlDoc, dataVersionBase ?: 0)

        val teamScheduleEntity: NbaTeamScheduleEntity = teamScheduleSourceObj?.let {
            TeamScheduleMapper.map(team, Gson().fromJson(teamScheduleSourceObj, TeamScheduleSource::class.java))
        } ?: NbaTeamScheduleEntity
                .emptyEntity(team, TimeStampUtil.getTimeVersionWithDayAndDataVersion(dataVersion = dataVersionBase).toLong())
        dbWriterService.writeTeamDetail(teamDetailEntity)
        dbWriterService.writeTeamSchedule(teamScheduleEntity)
        return teamScheduleEntity.events.map { TeamScheduleMapper.teamEventMapToEvent(it, teamDetailEntity) }
    }

    private fun mergeIntoList(eventSet: MutableSet<Event>, adding: List<Event>) {
        eventSet.addAll(adding)
    }
}