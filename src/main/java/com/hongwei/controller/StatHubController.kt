package com.hongwei.controller

import com.google.gson.Gson
import com.hongwei.constants.BadRequest
import com.hongwei.constants.ResetContent
import com.hongwei.service.nba.EspnCurlService
import com.hongwei.service.nba.EspnCurlService.Companion.TEAMS
import com.hongwei.service.nba.EspnStandingParseService
import com.hongwei.service.nba.JsonWriterService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/hub")
class StatHubController {
    @Autowired
    private lateinit var statCurlService: EspnCurlService

    @Autowired
    private lateinit var espnStandingParseService: EspnStandingParseService

    @Autowired
    private lateinit var jsonWriterService: JsonWriterService

    @GetMapping(path = ["/test.do"])
    @ResponseBody
    fun test(url: String): ResponseEntity<*> = statCurlService.curl(url)?.let {
        ResponseEntity.ok(it.toString())
    } ?: throw BadRequest

    @GetMapping(path = ["/espnStanding.do"])
    @ResponseBody
    fun getEspnStanding(): ResponseEntity<*> {
        val standingData = espnStandingParseService.parseStanding(
                statCurlService.getStanding()!!)
        return ResponseEntity.ok(Gson().toJson(standingData))
    }

    @PutMapping(path = ["/espnStanding.do"])
    @ResponseBody
    fun generateEspnStanding(): ResponseEntity<*> {
        val standingData = espnStandingParseService.parseStanding(
                statCurlService.getStanding()!!)
        standingData?.let {
            jsonWriterService.writeStanding(it)
        }
        return ResponseEntity.ok(null)
    }

    @GetMapping(path = ["/espnTeams.do"])
    @ResponseBody
    fun getEspnTeamShorts(): ResponseEntity<*> = ResponseEntity.ok(statCurlService.getTeamList())

    @GetMapping(path = ["/espnTeamSchedule.do"])
    @ResponseBody
    fun getEspnTeamSchedule(team: String, dataVersionBase: Int? = null): ResponseEntity<*> =
            statCurlService.getTeamScheduleJson(team, dataVersionBase ?: 0)?.let {
                ResponseEntity.ok(it)
            } ?: throw ResetContent

    @PutMapping(path = ["/espnTeamSchedule.do"])
    @ResponseBody
    fun generateEspnTeamSchedule(team: String, dataVersionBase: Int? = null): ResponseEntity<*> {
        val scheduleJsonString = statCurlService.getTeamScheduleJson(team, dataVersionBase ?: 0)
        scheduleJsonString?.let {
            jsonWriterService.writeTeamSchedule(team, scheduleJsonString)
        }
        return ResponseEntity.ok(null)
    }

    @PutMapping(path = ["/espnAllTeamsSchedule.do"])
    @ResponseBody
    fun generateEspnAllTeamSchedule(dataVersionBase: Int? = null): ResponseEntity<*> {
        TEAMS.forEach { team ->
            val scheduleJsonString = statCurlService.getTeamScheduleJson(team, dataVersionBase ?: 0)
            scheduleJsonString?.let {
                jsonWriterService.writeTeamSchedule(team, scheduleJsonString)
            }
        }
        return ResponseEntity.ok(null)
    }
}