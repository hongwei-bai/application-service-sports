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
                val dataVersion = TimeStampUtil.getTimeVersionWithMinute()
                "{\"dataVersion\": $dataVersion, \"data\": $dataString}"
            }

    fun parseTeamFixtures(document: Document?): String? =
            document?.toString()?.let { doc ->
                val index0 = doc.indexOf(SoccerFixturesOpen)
                val index1 = doc.indexOf(SoccerFixturesCloseExclusive)
                val mid = doc.substring(index0, index1)
                val end2 = mid.lastIndexOf("},")
                val mid2 = mid.substring(0, end2)
                mid2.replace(SoccerFixturesOpen, "")
            }

    fun parseTeamResults(document: Document?): String? =
            document?.toString()?.let { doc ->
                val index0 = doc.indexOf(SoccerResultsOpen)
                val index1 = doc.indexOf(SoccerResultsCloseExclusive)
                val mid = doc.substring(index0, index1)
                val end2 = mid.lastIndexOf("},")
                val mid2 = mid.substring(0, end2)
                mid2.replace(SoccerResultsOpen, "")
            }

    companion object {
        private const val SoccerStandingSourceWrap_Open_Exclusive = "window['__espnfitt__']="
        private const val SoccerStandingSourceWrap_Close_Inclusive = "\"scrollX\":0,\"scrollY\":0}}"

        private const val SoccerFixturesOpen = "\"fixtures\":"
        private const val SoccerFixturesCloseExclusive = "\"subType\":"

        private const val SoccerResultsOpen = "\"results\":"
        private const val SoccerResultsCloseExclusive = "\"subType\":"
    }
}