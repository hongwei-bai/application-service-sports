package com.hongwei.model.nba.espn.define

class EspnNbaScheduleQuery(val team: String) {
    private var seasonTypeId: SeasonTypeId? = null
    private var path = "https://www.espn.com/nba/team/schedule/_/name/$team"

    fun build(basePath: String = "https://www.espn.com/nba/team/schedule/_/name/$team"): String {
        return seasonTypeId?.let {
            "${basePath}/seasontype/${seasonTypeId!!.id}"
        } ?: basePath
    }
}