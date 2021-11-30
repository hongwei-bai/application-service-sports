package com.hongwei.controller

import com.hongwei.service.soccer.SoccerLeagueService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/soccer")
class SoccerController {
    @Autowired
    private lateinit var soccerLeagueService: SoccerLeagueService

    @GetMapping(path = ["/standing.do"])
    @ResponseBody
    fun getStandings(league: String, dataVersion: Long): ResponseEntity<*> =
            ResponseEntity.ok(soccerLeagueService.getStandings(league, dataVersion))

    @GetMapping(path = ["/teamSchedule.do"])
    @ResponseBody
    fun getTeamSchedule(team: String, dataVersion: Long): ResponseEntity<*> =
            ResponseEntity.ok(soccerLeagueService.getTeamSchedule(team, dataVersion))
}