package com.hongwei.controller

import com.google.gson.Gson
import com.hongwei.model.soccer.standing.SoccerStandingSourceOutput
import com.hongwei.service.soccer.EspnSoccerCurlService
import com.hongwei.service.soccer.EspnSoccerJsonWriterService
import com.hongwei.service.soccer.EspnSoccerParseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/hub/soccer")
class StatHubSoccerController {
    @Autowired
    private lateinit var soccerCurlService: EspnSoccerCurlService

    @Autowired
    private lateinit var soccerParseService: EspnSoccerParseService

    @Autowired
    private lateinit var jsonWriterService: EspnSoccerJsonWriterService

    @GetMapping(path = ["/espnStanding.do"])
    @ResponseBody
    fun getEspnStanding(): ResponseEntity<*> {
        val jsonString = soccerParseService.parseStandingInfo(soccerCurlService.getStanding())
        return ResponseEntity.ok(Gson().fromJson(jsonString, SoccerStandingSourceOutput::class.java))
    }

    @PutMapping(path = ["/espnStanding.do"])
    @ResponseBody
    fun generateEspnStanding(): ResponseEntity<*> {
        val jsonString = soccerParseService.parseStandingInfo(soccerCurlService.getStanding())
        jsonString?.let {
            jsonWriterService.writeStanding(it)
            return ResponseEntity.ok("Generate Serie A standing from ESPN successful.")
        }
        return ResponseEntity.ok("[ERROR]Failed to generate Serie A standing from ESPN.")
    }
}