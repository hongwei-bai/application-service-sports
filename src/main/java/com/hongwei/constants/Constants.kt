package com.hongwei.constants

object Constants {
    object Security {
        const val PUBLIC_ACCESS_STUB_USER = "public-access"
    }

    object AppDataPath {
        const val TEAM_SCHEDULE_JSON_PATH = "/schedule_{team}.json"

        const val STANDING_JSON_PATH = "/standing.json"

        const val TEAM_THEME_JSON_PATH = "/team_theme.json"

        const val TEAM_BANNER_URL = "https://hongwei-test1.top/resize/1080/nba_v1/banner/{team}.jpg"

        const val TEAM_BACKGROUND_URL = "https://hongwei-test1.top/resize/1080/nba_v1/background/{team}.jpg"
    }

    object TimeZone {
        const val UTC = "UTC"
        const val SYDNEY = "Australia/Sydney"
    }
}