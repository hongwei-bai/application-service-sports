package com.hongwei.controller

import com.hongwei.constants.ResetContent
import com.hongwei.service.nba.NbaPlayOffService
import com.hongwei.service.nba.NbaService
import com.hongwei.service.nba.NbaThemeService
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

    @Autowired
    private lateinit var nbaThemeService: NbaThemeService

    @Autowired
    private lateinit var nbaPlayOffService: NbaPlayOffService

    @GetMapping(path = ["/teamSchedule.do"])
    @ResponseBody
    fun getTeamSchedule(team: String, dataVersion: Long): ResponseEntity<*> =
            nbaService.getScheduleByTeam(team, dataVersion)?.let {
                ResponseEntity.ok(it)
            } ?: throw ResetContent

    @GetMapping(path = ["/standing.do"])
    @ResponseBody
    fun getStanding(dataVersion: Long): ResponseEntity<*> =
            nbaService.getStanding(dataVersion)?.let {
                ResponseEntity.ok(it)
            } ?: throw ResetContent

    @GetMapping(path = ["/schedule.do"])
    @ResponseBody
    fun getFullSchedule(dataVersion: Long): ResponseEntity<*> =
            nbaService.getFullSchedule(dataVersion)?.let {
                ResponseEntity.ok(it)
            } ?: throw ResetContent

    @GetMapping(path = ["/playOff.do"])
    @ResponseBody
    fun getPlayOff(dataVersion: Long): ResponseEntity<*> =
            nbaPlayOffService.getPlayOff(dataVersion)?.let {
                ResponseEntity.ok(it)
            } ?: throw ResetContent

    @GetMapping(path = ["/teamTheme.do"])
    @ResponseBody
    fun getTeamTheme(team: String, dataVersion: Long): ResponseEntity<*> =
            nbaThemeService.getTeamTheme(team, dataVersion)?.let {
                ResponseEntity.ok(it)
            } ?: throw ResetContent
}