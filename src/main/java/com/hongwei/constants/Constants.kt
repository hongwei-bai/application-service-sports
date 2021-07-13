package com.hongwei.constants

object Constants {
    object Security {
        const val PUBLIC_ACCESS_STUB_USER = "public-access"
    }

    object AppDataPath {
        // NBA
        const val NBA_DATA_PATH = "nba_v1"
        const val TEAM_THEME_JSON_PATH = "/team_theme.json"
        const val TEAM_BANNER_URL = "https://hongwei-test1.top/resize/1080/nba_v1/banner/{team}.jpg"
        const val TEAM_BACKGROUND_URL = "https://hongwei-test1.top/resize/1080/nba_v1/background/{team}.jpg"

        // Soccer
        const val SOCCER_DATA_PATH = "soccer_v1"
        const val SOCCER_LEAGUE_JSON_PATH = "/soccer_league.json"
    }

    object TimeZone {
        const val UTC = "UTC"
        const val SYDNEY = "Australia/Sydney"
    }
}