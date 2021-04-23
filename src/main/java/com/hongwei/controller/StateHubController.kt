package com.hongwei.controller

import com.hongwei.constants.ResetContent
import com.hongwei.service.nba.EspnCurlService
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