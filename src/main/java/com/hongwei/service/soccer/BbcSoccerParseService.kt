package com.hongwei.service.soccer

import com.hongwei.service.nba.EspnStandingParseService
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.jsoup.nodes.Document
import org.springframework.stereotype.Service

@Service
class BbcSoccerParseService {
    private val logger: Logger = LogManager.getLogger(EspnStandingParseService::class.java)

    fun parseMonthSchedule(document: Document?): String? =
            document?.toString()?.let { doc ->
                val index0 = doc.indexOf(SoccerResultsOpen)
                val midTail = doc.substring(index0)
                val index1 = midTail.indexOf(SoccerResultsCloseExclusive)
                val mid = midTail.substring(0, index1)
                val lastTrimIdx = mid.lastIndexOf("});")
                val mid2 = mid.substring(0, lastTrimIdx)
                "{$mid2"
            }

    companion object {
        private const val SoccerResultsOpen = "\"matchData\":"
        private const val SoccerResultsCloseExclusive = ";</script>"
    }
}