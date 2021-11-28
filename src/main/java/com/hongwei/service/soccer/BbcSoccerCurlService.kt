package com.hongwei.service.soccer

import com.hongwei.curl.CUrlWrapper
import com.hongwei.model.soccer.bbc.BbcSoccerScheduleQuery
import org.jsoup.nodes.Document
import org.springframework.stereotype.Service

@Service
class BbcSoccerCurlService {
    fun curl(url: String): Document? = CUrlWrapper.curl(url)

    fun getSchedule(year: Int, monthNumber: Int): Document? = CUrlWrapper.curl(BbcSoccerScheduleQuery(year, monthNumber).build())
}