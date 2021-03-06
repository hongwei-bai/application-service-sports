package com.hongwei.service.nba

import com.hongwei.constants.Endpoints
import com.hongwei.curl.CUrlWrapper
import com.hongwei.model.nba.espn.define.EspnNbaScheduleQuery
import com.hongwei.util.TimeStampUtil
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.stereotype.Service
import org.jsoup.nodes.Document

@Service
class EspnCurlService {
    private val logger: Logger = LogManager.getLogger(EspnCurlService::class.java)

    fun curl(url: String): Document? = CUrlWrapper.curl(url)

    fun getStanding(): Document? = CUrlWrapper.curl(Endpoints.Espn.STANDING)

    fun getTransactions(): Document? = CUrlWrapper.curl(Endpoints.Espn.TRANSACTIONS)

    fun getTeamList(): List<String> = TEAMS.toList()

    fun getTeamScheduleJson(curlDoc: String): String? {
        val index0 = curlDoc.indexOf(TEAM_SCHEDULE_START)
        val index1 = curlDoc.indexOf(TEAM_SCHEDULE_END)
        if (index0 > 0 && index1 > 0) {
            val mid = curlDoc.substring(index0, index1 + TEAM_SCHEDULE_END.length)
            val dataVersion = TimeStampUtil.getTimeVersionWithMinute()
            return "{\"dataVersion\": $dataVersion, $mid}"
        }
        return null
    }

    fun getTeamDetailJson(curlDoc: String): String? {
        val index0 = curlDoc.indexOf(TEAM_DETAIL_START)
        val index1 = curlDoc.indexOf(TEAM_DETAIL_END_EXCLUDE)
        return curlDoc.substring(index0, index1).trim().substringBeforeLast(",").replace(TEAM_DETAIL_START, "").trim()
    }

    fun getTransactionsJson(curlDoc: String): String? {
        val index0 = curlDoc.indexOf(TRANSACTIONS_START_EXCLUDE)
        val index1 = curlDoc.indexOf(TRANSACTIONS_END_EXCLUDE)
        if (index0 > 0 && index1 > 0) {
            return curlDoc.substring(index0 + TRANSACTIONS_START_EXCLUDE.length, index1 - 1)
        }
        return null
    }

    fun getTeamScheduleCurlDoc(teamShort: String): String = CUrlWrapper.curl(EspnNbaScheduleQuery(teamShort).build()).toString()

    fun getPlayedPlayInTeamScheduleCurlDoc(teamShort: String): String = CUrlWrapper.curl(EspnNbaScheduleQuery(teamShort).playIn().build()).toString()

    companion object {
        private const val TEAM_SCHEDULE_START = "\"teamSchedule\":"
        private const val TEAM_SCHEDULE_END = "\"No Data Available\""

        private const val TEAM_DETAIL_START = "\"team\":"
        private const val TEAM_DETAIL_END_EXCLUDE = "\"groups\":"

        private const val TRANSACTIONS_START_EXCLUDE = "\"transactions\":"
        private const val TRANSACTIONS_END_EXCLUDE = "\"requestParams\":"

        val TEAMS = arrayOf(
                "atl",
                "bkn",
                "bos",
                "cha",
                "chi",
                "cle",
                "dal",
                "den",
                "det",
                "gs",
                "hou",
                "ind",
                "lac",
                "lal",
                "mem",
                "mia",
                "mil",
                "min",
                "no",
                "ny",
                "okc",
                "orl",
                "phi",
                "phx",
                "por",
                "sac",
                "sa",
                "tor",
                "utah",
                "wsh")
    }
}