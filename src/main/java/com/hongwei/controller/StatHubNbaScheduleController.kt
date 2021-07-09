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
import java.util.*

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
        val playOffData = nbaPlayOffService.getPlayOff(0)
        val eventSet = mutableSetOf<Event>()
        var message = "[ERROR]Nothing happen in this api."
        when {
            playOffData?.playInOngoing == true -> {
                val standing = nbaService.getStanding(0)
                message = if (standing != null) {
                    val playInTeams = (standing.western + standing.eastern)
                            .filter { it.rank in 7..10 }
                            .map { it.teamAbbr.toLowerCase(Locale.US) }
                    playInTeams.forEach { team ->
                        mergeIntoList(eventSet, generateScheduleForEachTeam(team, dataVersionBase))
                    }
                    "Play-in Tournament: Schedules for following teams are generated: ${playInTeams.joinToString(",")}"
                } else {
                    "[ERROR]Play-in Tournament: standing data is null, failed to generate play-in schedules."
                }
            }
            playOffData?.playOffOngoing == true -> {
                val standing = nbaService.getStanding(0)
                val playInData = nbaPlayOffService.getPlayOff(0)?.playIn
                val westernSeed7 = playInData?.western?.winnerOf78 ?: "TBD"
                val westernSeed8 = playInData?.western?.lastWinner ?: "TBD"
                val easternSeed7 = playInData?.eastern?.winnerOf78 ?: "TBD"
                val easternSeed8 = playInData?.eastern?.lastWinner ?: "TBD"
                message = if (standing != null && westernSeed7 != "TBD" && westernSeed8 != "TBD" && easternSeed7 != "TBD" && easternSeed8 != "TBD") {
                    val playOffTeams = (standing.western + standing.eastern)
                            .filter { it.rank in 1..6 }
                            .map { it.teamAbbr }.map { it.toLowerCase(Locale.US) } +
                            listOf(westernSeed7, westernSeed8, easternSeed7, easternSeed8).map { it.toLowerCase(Locale.US) }
                    playOffTeams.forEach { team ->
                        mergeIntoList(eventSet, generateScheduleForEachTeam(team, dataVersionBase))
                    }
                    "PlayOff: Schedules for following teams are generated: ${playOffTeams.joinToString(",")}"
                } else {
                    "[ERROR]PlayOff: standing or play-in result data is null, failed to generate playoff schedules."
                }
            }
            else -> {
                TEAMS.forEach { team ->
                    mergeIntoList(eventSet, generateScheduleForEachTeam(team, dataVersionBase))
                }
                message = "Regular season: ${TEAMS.size} teams' schedule generated."
            }
        }

        eventSet.sortedByDescending { it.unixTimeStamp }
        val dataVersion = TimeStampUtil.getTimeVersionWithDayAndDataVersion(dataVersion = dataVersionBase)
        dbWriterService.writeFullSchedule(dataVersion, eventSet.toList())
        return ResponseEntity.ok(message)
    }

    @PutMapping(path = ["/espnTeamSchedule.do"])
    @ResponseBody
    fun generateEspnTeamSchedule(team: String, dataVersionBase: Int? = null): ResponseEntity<*> {
        generateScheduleForEachTeam(team, dataVersionBase)
        return ResponseEntity.ok(null)
    }

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