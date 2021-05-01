package com.hongwei.model.nba.theme

data class NbaTeamTheme(
        val dataVersion: Long,
        val team: String,
        val bannerUrl: String,
        val colorLight: Long? = null,
        val colorHome: Long? = null,
        val colorGuest: Long? = null
)