package com.hongwei.service.soccer

import com.hongwei.constants.Endpoints
import com.hongwei.curl.CUrlWrapper
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.stereotype.Service
import org.jsoup.nodes.Document

@Service
class EspnSoccerCurlService {
    private val logger: Logger = LogManager.getLogger(EspnSoccerCurlService::class.java)

    fun curl(url: String): Document? = CUrlWrapper.curl(url)

    fun getStanding(): Document? = CUrlWrapper.curl(Endpoints.Espn.Soccer.SERIEA_STANDING)

    companion object {
        const val TeamUrlBase = "https://www.espn.com.au/"

        val SoccerStandingHeadersSource =
                listOf("wins", "Losses", "Draws", "Games Played",
                        "Goals For", "Goals Against", "Points",
                        "Rank Change", "Rank", "Goal Difference", "Point Deductions",
                        "Points Per Game", "Overall Record")

        val SoccerStandingHeadersAbbrSource =
                listOf("W", "L", "D", "GP",
                        "F", "A", "P",
                        "Rank Change", "Rank", "GD", "Deductions",
                        "PPG", "OVER")
    }
}