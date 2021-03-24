package com.hongwei.controller

import com.hongwei.service.NbaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/nba")
@CrossOrigin
class NbaController {
    @Autowired
    private lateinit var nbaService: NbaService

    @PutMapping(path = ["/teamSchedule.do"])
    @ResponseBody
    fun registerUser(): ResponseEntity<*> = ResponseEntity.ok(nbaService.getStubSchedule())
}