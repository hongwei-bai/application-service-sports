package com.hongwei.model.soccer.espn.define

class EspnSoccerScheduleQuery(val teamId: Long, val type: SoccerQueryType) {
    private var leagueString: String? = null
    private var path = "https://www.espn.com/soccer/team/${type.path}/_/id/$teamId"

    //Standing: (Where teamId comes from)
    //https://www.espn.com/soccer/league/_/name/ita.1

    //Schedule:
    //https://www.espn.com/soccer/team/fixtures/_/id/103
    //https://www.espn.com/soccer/team/results/_/id/103/league/ITA.COPPA_ITALIA

    fun all(): EspnSoccerScheduleQuery = this.apply { this.leagueString = null }

    fun league(league: String): EspnSoccerScheduleQuery = this.apply { this.leagueString = league.toUpperCase() }

    fun league(league: SoccerLeague): EspnSoccerScheduleQuery = this.apply { this.leagueString = league.path }

    fun build(): String {
        return leagueString?.let {
            "$path/league/$leagueString"
        } ?: path
    }
}