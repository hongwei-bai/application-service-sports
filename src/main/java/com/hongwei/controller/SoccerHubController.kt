package com.hongwei.controller

import com.hongwei.service.soccer.SoccerAnalysisService
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/hub/soccer")
class SoccerHubController {
    private val logger: Logger = LogManager.getLogger(SoccerHubController::class.java)

    @Autowired
    private lateinit var soccerAnalysisService: SoccerAnalysisService

    @RequestMapping(path = ["/espnStanding.do"])
    @ResponseBody
    fun generateEspnStanding(league: String, downloadLogos: Boolean? = null): ResponseEntity<*> {
        return ResponseEntity.ok(soccerAnalysisService.fetchStandings(league, downloadLogos ?: false))
    }

    @RequestMapping(path = ["/espnTeamSchedule.do"])
    @ResponseBody
    fun generateEspnTeamSchedule(teamId: Long): ResponseEntity<*> {
        return ResponseEntity.ok(soccerAnalysisService.fetchTeamSchedules(teamId))
    }

    @RequestMapping(path = ["/bbcSchedule.do"])
    @ResponseBody
    fun generateBbcSchedule(): ResponseEntity<*> {
        return ResponseEntity.ok(soccerAnalysisService.fetchChampionLeagueSchedules())
    }
}