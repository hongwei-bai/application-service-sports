package com.hongwei.controller

import com.hongwei.service.soccer.SoccerLeagueService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/soccer/uefa")
class SoccerUEFAController {
    @Autowired
    private lateinit var soccerLeagueService: SoccerLeagueService

    @GetMapping(path = ["/schedule.do"])
    @ResponseBody
    fun getUEFASchedule(dataVersion: Long): ResponseEntity<*> =
            ResponseEntity.ok(dataVersion)
}