package com.hongwei.service.nba

import com.hongwei.curl.CUrlWrapper
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.stereotype.Service
import java.util.*
import java.util.Calendar.*

@Service
class EspnCurlService {
    private val logger: Logger = LogManager.getLogger(EspnCurlService::class.java)

    fun getTeamList(): List<String> = TEAMS.toList()

    fun getTeamScheduleJson(teamShort: String, dataVersionBase: Int): String? {
        val url = "https://www.espn.com/nba/team/schedule/_/name/$teamShort"
        val doc = CUrlWrapper.curl(url).toString()
        val index0 = doc.indexOf(START)
        val index1 = doc.indexOf(END)
        val mid = doc.substring(index0, index1 + END.length)
        val date = getInstance()
        val dataVersion = "${date.get(YEAR)}" +
                (date.get(MONTH) + 1).toString().padStart(2, '0') +
                (date.get(DAY_OF_MONTH)).toString().padStart(2, '0') +
                dataVersionBase.toString().padStart(2, '0')
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