package com.hongwei.service.nba

import com.hongwei.constants.Endpoints
import com.hongwei.curl.CUrlWrapper
import com.hongwei.util.TimeStampUtil
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.stereotype.Service
import java.util.Calendar.*
import org.jsoup.nodes.Document

@Service
class EspnCurlService {
    private val logger: Logger = LogManager.getLogger(EspnCurlService::class.java)

    fun curl(url: String): Document? = CUrlWrapper.curl(url)

    fun getStanding(): Document? = CUrlWrapper.curl(Endpoints.Espn.STANDING)

    fun getTeamList(): List<String> = TEAMS.toList()

    fun getTeamScheduleJson(teamShort: String, dataVersionBase: Int): String? {
        val url = Endpoints.Espn.TEAM_SCHEDULE.replace("{team}", teamShort)
        val doc = CUrlWrapper.curl(url).toString()
        val index0 = doc.indexOf(START)
        val index1 = doc.indexOf(END)
        val mid = doc.substring(index0, index1 + END.length)
        val dataVersion = TimeStampUtil.getTimeVersionWithDayAndDataVersion(dataVersion = dataVersionBase)
        return "{\"dataVersion\": $dataVersion, $mid}"
    }

    companion object {
        private val START = "\"teamSchedule\":"
        private val END = "\"No Data Available\""

        val TEAMS = arrayOf(
                "gs",
                "dal",
                "den",
                "det",
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