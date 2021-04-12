package com.hongwei.controller

import com.hongwei.constants.ResetContent
import com.hongwei.service.nba.NbaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/nba")
class NbaController {
    @Autowired
    private lateinit var nbaService: NbaService

    @GetMapping(path = ["/teamSchedule.do"])
    @ResponseBody
    fun testGetTeamSchedule(team: String, dataVersion: Long): ResponseEntity<*> =
            nbaService.getScheduleByTeam(team, dataVersion)?.let {
                ResponseEntity.ok(it)
            } ?: throw ResetContent
}