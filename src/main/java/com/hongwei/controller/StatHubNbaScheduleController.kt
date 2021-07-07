package com.hongwei.controller

import com.hongwei.constants.BadRequest
import com.hongwei.constants.ResetContent
import com.hongwei.service.nba.*
import com.hongwei.service.nba.EspnCurlService.Companion.TEAMS
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
    private lateinit var jsonWriterService: JsonWriterService

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
            statCurlService.getTeamScheduleJson(team, dataVersionBase ?: 0)?.let {
                ResponseEntity.ok(it)
            } ?: throw ResetContent

    @PutMapping(path = ["/espnAllTeamsSchedule.do"])
    @ResponseBody
    fun generateEspnAllTeamSchedule(dataVersionBase: Int? = null): ResponseEntity<*> {
        val playOffData = nbaPlayOffService.getPlayOff(0)
        var message = "[ERROR]Nothing happen in this api."
        if (playOffData?.playInOngoing == true) {
            val standing = nbaService.getStanding(0)
            if (standing != null) {
                val playInTeams = (standing.western.teams + standing.eastern.teams)
                        .filter { it.rank in 7..10 }
                        .map { it.teamAbbr.toLowerCase(Locale.US) }
                playInTeams.forEach { team ->
                    generateScheduleForEachTeam(team, dataVersionBase)
                }
                message = "Play-in Tournament: Schedules for following teams are generated: ${playInTeams.joinToString(",")}"
            } else {
                message = "[ERROR]Play-in Tournament: standing data is null, failed to generate play-in schedules."
            }
        } else if (playOffData?.playOffOngoing == true) {
            val standing = nbaService.getStanding(0)
            val playInData = nbaPlayOffService.getPlayOff(0)?.playIn
            val westernSeed7 = playInData?.western?.winnerOf78 ?: "TBD"
            val westernSeed8 = playInData?.western?.lastWinner ?: "TBD"
            val easternSeed7 = playInData?.eastern?.winnerOf78 ?: "TBD"
            val easternSeed8 = playInData?.eastern?.lastWinner ?: "TBD"
            if (standing != null && westernSeed7 != "TBD" && westernSeed8 != "TBD" && easternSeed7 != "TBD" && easternSeed8 != "TBD") {
                val playOffTeams = (standing.western.teams + standing.eastern.teams)
                        .filter { it.rank in 1..6 }
                        .map { it.teamAbbr }.map { it.toLowerCase(Locale.US) } +
                        listOf(westernSeed7, westernSeed8, easternSeed7, easternSeed8).map { it.toLowerCase(Locale.US) }
                playOffTeams.forEach { team ->
                    generateScheduleForEachTeam(team, dataVersionBase)
                }
                message = "PlayOff: Schedules for following teams are generated: ${playOffTeams.joinToString(",")}"
            } else {
                message = "[ERROR]PlayOff: standing or play-in result data is null, failed to generate playoff schedules."
            }
        } else {
            TEAMS.forEach { team ->
                generateScheduleForEachTeam(team, dataVersionBase)
            }
            message = "Regular season: ${TEAMS.size} teams' schedule generated."
        }
        return ResponseEntity.ok(message)
    }

    @PutMapping(path = ["/espnTeamSchedule.do"])
    @ResponseBody
    fun generateEspnTeamSchedule(team: String, dataVersionBase: Int? = null): ResponseEntity<*> {
        generateScheduleForEachTeam(team, dataVersionBase)
        return ResponseEntity.ok(null)
    }

    private fun generateScheduleForEachTeam(team: String, dataVersionBase: Int?, storageType: StorageType? = StorageType.Db) {
        val curlDoc = statCurlService.getTeamScheduleCurlDoc(team)
        val teamDetailJson = statCurlService.getTeamDetailJson(curlDoc)
        val scheduleJsonString = statCurlService.getTeamScheduleJson(curlDoc, dataVersionBase ?: 0)
        if (teamDetailJson != null && scheduleJsonString != null) {
            when (storageType) {
                StorageType.Db -> dbWriterService.writeTeamSchedule(teamDetailJson, scheduleJsonString)
                StorageType.Json -> jsonWriterService.writeTeamSchedule(teamDetailJson, scheduleJsonString)
            }
        }
    }

    enum class StorageType {
        Json, Db
    }
}