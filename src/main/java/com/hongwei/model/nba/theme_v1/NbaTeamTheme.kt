package com.hongwei.model.nba.theme_v1

data class NbaTeamTheme(
        val dataVersion: Long,
        val team: String,
        val bannerUrl: String,
        val backgroundUrl: String? = null,
        val colorLight: Long? = null,
        val colorHome: Long? = null,
        val colorGuest: Long? = null,
        val teamColor: Long? = null,
        val altColor: Long? = null
)