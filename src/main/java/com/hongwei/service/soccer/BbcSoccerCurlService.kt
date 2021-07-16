package com.hongwei.service.soccer

import com.hongwei.curl.CUrlWrapper
import com.hongwei.model.soccer.bbc.BbcSoccerScheduleQuery
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.jsoup.nodes.Document
import org.springframework.stereotype.Service

@Service
class BbcSoccerCurlService {
    private val logger: Logger = LogManager.getLogger(BbcSoccerCurlService::class.java)

    fun curl(url: String): Document? = CUrlWrapper.curl(url)

    fun getSchedule(): Document? = CUrlWrapper.curl(BbcSoccerScheduleQuery(2021, 7).build())
}