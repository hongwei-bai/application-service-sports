package com.hongwei.service.nba

import com.hongwei.model.nba.ConferenceStanding
import com.hongwei.model.nba.Standing
import com.hongwei.model.nba.TeamStanding
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class EspnStandingParseService {
    private val logger: Logger = LogManager.getLogger(EspnStandingParseService::class.java)

    @Autowired
    private lateinit var espnTeamMapper: EspnTeamMapper

    fun parseStanding(document: Document): Standing? {
        // For: <div class="standings__table InnerLayout__child--dividers standings__table--nba-play-in-tournament">
        val standingRoot = document.getElementsByClass("standings__table")
        if (standingRoot.size != 2) {
            return null
        }

        /*
        <div class="Table__Title">
             Eastern Conference
        </div> */
        return Standing(
                parseConferenceStanding(standingRoot[0]),
                parseConferenceStanding(standingRoot[1])
        )
    }

    private fun parseConferenceStanding(conference: Element): ConferenceStanding {
        val conferenceTitle = conference.getElementsByClass("Table__Title").select("div").text()
        /*
        <span class="team-position ml2 pr3">1</span><span class="pr4 TeamLink__Logo"><a class="AnchorLink" tabindex="0" data-clubhouse-uid="s:40~l:46~t:17"
        href="/nba/team/_/name/bkn/brooklyn-nets"><img alt="Brooklyn Nets" class="Image Logo Logo__sm" title="Brooklyn Nets" data-mptype="image"
        src="data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7"></a></span><span class="dn show-mobile">
        <a class="AnchorLink" tabindex="0" data-clubhouse-uid="s:40~l:46~t:17" href="/nba/team/_/name/bkn/brooklyn-nets">
        <abbr style="text-decoration:none" title="Brooklyn Nets" data-clubhouse-uid="s:40~l:46~t:17">BKN</abbr></a></span>
        <span class="hide-mobile"><a class="AnchorLink" tabindex="0" data-clubhouse-uid="s:40~l:46~t:17" href="/nba/team/_/name/bkn/brooklyn-nets">Brooklyn Nets</a></span>
         */
        val teams = conference.getElementsByClass("Table__TD")
        val resultTeams = mutableListOf<TeamStanding>()
        teams.forEach { team ->
            val rank = team.selectFirst("span").text()
            val teamAbbr = team.getElementsByClass("Table__TD")
            if (teamAbbr.size >= 1) {
                val teamDisplayName = teamAbbr[0].select("abbr").attr("title")
                val abbr = teamAbbr[0].select("abbr").text()
                if (!teamDisplayName.isNullOrEmpty() && !abbr.isNullOrEmpty()) {
                    resultTeams.add(TeamStanding(rank, teamDisplayName, abbr, espnTeamMapper.teamShortMapToLegacy(abbr), mutableListOf()))
                }
            }
        }

        val sections = conference.getElementsByClass("Table__sub-header")
        val detailSection = sections[1]
        val headers = detailSection.getElementsByClass("Table__TH")
        val resultHeaders = mutableListOf<String>()
        val resultHeaderAbbrs = mutableListOf<String>()
        headers.forEach { header ->
            val headerFullName = header.select("span").attr("title")
            var headerShortName = header.getElementsByClass("AnchorLink").select("a").text()
            if (headerShortName.isNullOrEmpty()) {
                headerShortName = header.select("span").text()
            }
            resultHeaders.add(headerFullName)
            resultHeaderAbbrs.add(headerShortName)
        }

        val teamDataSection = conference.getElementsByClass("Table__ScrollerWrapper")[0]
        teamDataSection.getElementsByClass("Table__TR--sm").forEach { teamData ->
            val position = teamData.select("tr").attr("data-idx").toInt()
            teamData.getElementsByClass("stat-cell").forEach {
                val number = it.select("span").text()
                resultTeams[position].detail.add(number)
            }
        }

        return ConferenceStanding(conferenceTitle, resultTeams, resultHeaders, resultHeaderAbbrs)
    }
}