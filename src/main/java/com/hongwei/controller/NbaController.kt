package com.hongwei.controller

import com.hongwei.constants.InternalServerError
import com.hongwei.constants.ResetContent
import com.hongwei.service.nba.NbaPostSeasonService
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
    private lateinit var nbaPostSeasonService: NbaPostSeasonService

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

    @GetMapping(path = ["/postSeason.do"])
    @ResponseBody
    fun getPlayOff(dataVersion: Long): ResponseEntity<*> =
            nbaPostSeasonService.getPostSeason(dataVersion)?.let {
                ResponseEntity.ok(it)
            } ?: throw InternalServerError

    @GetMapping(path = ["/seasonStatus.do"])
    @ResponseBody
    fun getSeasonStatus(): ResponseEntity<*> =
            nbaPostSeasonService.getSeasonStatus()?.let {
                ResponseEntity.ok(it)
            } ?: throw InternalServerError

    @GetMapping(path = ["/transactions.do"])
    @ResponseBody
    fun getTransactions(dataVersion: Long): ResponseEntity<*> =
            nbaService.getTransactions(dataVersion)?.let {
                ResponseEntity.ok(it)
            } ?: throw ResetContent

    @Deprecated("NBA_V1")
    @GetMapping(path = ["/teamTheme.do"])
    @ResponseBody
    fun getTeamTheme(team: String, dataVersion: Long): ResponseEntity<*> =
            nbaThemeService.getTeamTheme(team, dataVersion)?.let {
                ResponseEntity.ok(it)
            } ?: throw ResetContent

    @GetMapping(path = ["/teamDetail.do"])
    @ResponseBody
    fun getTeamTheme(team: String): ResponseEntity<*> =
            nbaThemeService.getTeamDetail(team)?.let {
                ResponseEntity.ok(it)
            } ?: throw ResetContent
}