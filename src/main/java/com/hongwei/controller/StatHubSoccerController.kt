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
class StatHubSoccerController {
    private val logger: Logger = LogManager.getLogger(StatHubSoccerController::class.java)

    @Autowired
    private lateinit var soccerAnalysisService: SoccerAnalysisService

    @RequestMapping(path = ["/espnStanding.do"])
    @ResponseBody
    fun generateEspnStanding(league: String): ResponseEntity<*> {
        return ResponseEntity.ok(soccerAnalysisService.fetchStandings(league))
    }

    @RequestMapping(path = ["/espnTeamFixtures.do"])
    @ResponseBody
    fun generateEspnTeamFixtures(teamId: Int): ResponseEntity<*> {
        return ResponseEntity.ok(soccerAnalysisService.fetchTeamFixtures(teamId))
    }

    @RequestMapping(path = ["/espnTeamResults.do"])
    @ResponseBody
    fun generateEspnTeamResults(teamId: Int): ResponseEntity<*> {
        return ResponseEntity.ok(soccerAnalysisService.fetchTeamResults(teamId))
    }
}