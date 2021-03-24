package com.hongwei.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/nba")
@CrossOrigin
class NbaController {
    @PutMapping(path = ["/teamSchedule.do"])
    @ResponseBody
    fun registerUser(): ResponseEntity<*> = ResponseEntity.ok(null)
}