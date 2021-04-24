package com.hongwei.controller

import com.google.gson.Gson
import com.hongwei.constants.BadRequest
import com.hongwei.constants.ResetContent
import com.hongwei.service.nba.EspnCurlService
import com.hongwei.service.nba.EspnStandingParseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/hub")
class StateHubController {
    @Autowired
    private lateinit var statCurlService: EspnCurlService

    @Autowired
    private lateinit var espnStandingParseService: EspnStandingParseService

    @GetMapping(path = ["/test.do"])
    @ResponseBody
    fun test(url: String): ResponseEntity<*> = statCurlService.test(url)?.let {
        ResponseEntity.ok(it.toString())
    } ?: throw BadRequest

    @GetMapping(path = ["/espnStanding.do"])
    @ResponseBody
    fun getEspnStanding(): ResponseEntity<*> {
        val standingData = espnStandingParseService.parseStanding(
                statCurlService.test("https://www.espn.com/nba/standings")!!)
        return ResponseEntity.ok(Gson().toJson(standingData))
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
}