package com.hongwei.controller

import com.google.gson.Gson
import com.hongwei.service.nba.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/hub")
class StatHubNbaStandingController {
    @Autowired
    private lateinit var statCurlService: EspnCurlService

    @Autowired
    private lateinit var espnStandingParseService: EspnStandingParseService

    @Autowired
    private lateinit var jsonWriterService: JsonWriterService

    @Autowired
    private lateinit var dbWriterService: DbWriterService

    @GetMapping(path = ["/espnStanding.do"])
    @ResponseBody
    fun getEspnStanding(): ResponseEntity<*> {
        val standingData = espnStandingParseService.parseStanding(
                statCurlService.getStanding()!!)
        return ResponseEntity.ok(Gson().toJson(standingData))
    }

    @PutMapping(path = ["/espnStandingJson.do"])
    @ResponseBody
    fun generateEspnStandingJson(): ResponseEntity<*> {
        val standingData = espnStandingParseService.parseStanding(
                statCurlService.getStanding()!!)
        standingData?.let {
            jsonWriterService.writeStanding(it)
        }
        return ResponseEntity.ok(null)
    }

    @PutMapping(path = ["/espnStanding.do"])
    @ResponseBody
    fun generateEspnStandingDb(): ResponseEntity<*> {
        val standingData = espnStandingParseService.parseStanding(
                statCurlService.getStanding()!!)
        standingData?.let {
            dbWriterService.writeStanding(it)
        }
        return ResponseEntity.ok(null)
    }
}