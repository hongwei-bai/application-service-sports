package com.hongwei.model.soccer.espn.define

class EspnSoccerScheduleQuery(val teamId: Int, val type: SoccerQueryType) {
    private var league: SoccerLeague? = null
    private var path = "https://www.espn.com/soccer/team/${type.path}/_/id/$teamId"

    //Standing: (Where teamId comes from)
    //https://www.espn.com/soccer/league/_/name/ita.1

    //Schedule:
    //https://www.espn.com/soccer/team/fixtures/_/id/103
    //https://www.espn.com/soccer/team/results/_/id/103/league/ITA.COPPA_ITALIA

    fun all(): EspnSoccerScheduleQuery = this.apply { this.league = null }

    fun league(league: SoccerLeague): EspnSoccerScheduleQuery = this.apply { this.league = league }

    fun build(): String {
        return league?.let {
            "$path/league/${it.path}"
        } ?: path
    }
}