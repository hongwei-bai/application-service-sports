package com.hongwei.service.soccer

import com.hongwei.service.nba.EspnStandingParseService
import com.hongwei.util.TimeStampUtil
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.jsoup.nodes.Document
import org.springframework.stereotype.Service

@Service
class EspnSoccerParseService {
    private val logger: Logger = LogManager.getLogger(EspnStandingParseService::class.java)

    fun parseStandingInfo(document: Document?): String? =
            document?.toString()?.let { doc ->
                val index0 = doc.indexOf(SoccerStandingSourceWrap_Open_Exclusive)
                val index1 = doc.indexOf(SoccerStandingSourceWrap_Close_Inclusive)
                val mid = doc.substring(index0, index1 + SoccerStandingSourceWrap_Close_Inclusive.length)
                val dataString = mid.replace(SoccerStandingSourceWrap_Open_Exclusive, "")
                val dataVersion = TimeStampUtil.getTimeVersionWithHour()
                "{\"dataVersion\": $dataVersion, $dataString}"
            }

    companion object {
        private const val SoccerStandingSourceWrap_Open_Exclusive = "window['__espnfitt__']="
        private const val SoccerStandingSourceWrap_Close_Inclusive = "\"scrollX\":0,\"scrollY\":0}}"
    }
}