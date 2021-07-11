package com.hongwei.model.nba.espn.define

class EspnNbaScheduleQuery(val team: String) {
    private var seasonTypeId: SeasonTypeId? = null
    private var path = "https://www.espn.com/nba/team/schedule/_/name/$team"

    fun playIn(): EspnNbaScheduleQuery = this.apply { seasonTypeId = SeasonTypeId.PlayInTournament }

    fun playOff(): EspnNbaScheduleQuery = this.apply { seasonTypeId = SeasonTypeId.PostSeason }

    fun build(basePath: String = "https://www.espn.com/nba/team/schedule/_/name/$team"): String {
        return seasonTypeId?.let {
            "${basePath}/seasontype/${seasonTypeId!!.id}"
        } ?: basePath
    }
}