package com.hongwei.model.nba.theme_v1

object NbaTeamThemeMapper {
    fun map(teamThemeJson: NbaTeamThemeJson, team: String): NbaTeamTheme? = null
//            teamThemeJson.data.firstOrNull {
//                it.team == team
//            }?.let {
//                NbaTeamTheme(
//                        dataVersion = teamThemeJson.dataVersion ?: 0,
//                        team = team,
//                        bannerUrl = TEAM_BANNER_URL.replace("{team}", team),
//                        backgroundUrl = TEAM_BACKGROUND_URL.replace("{team}", team),
//                        colorLight = it.colorLight?.mapToLong(),
//                        colorHome = it.colorHome?.mapToLong(),
//                        colorGuest = it.colorGuest?.mapToLong()
//                )
//            } ?: NbaTeamTheme(
//                    dataVersion = teamThemeJson.dataVersion ?: 0,
//                    team = team,
//                    bannerUrl = TEAM_BANNER_URL.replace("{team}", team)
//            )

    private fun String.mapToLong(): Long? {
        var string: String = this
        if (string.startsWith("0x", true)) {
            string = string.replace("0x", "", true)
        }

        if (string.length == 6) {
            string = "FF$string"
        }

        return string.toLongOrNull(16) ?: 0xFFFFFFFF
    }
}