package com.hongwei.constants

object Constants {
    object Security {
        const val PUBLIC_ACCESS_STUB_USER = "public-access"
    }

    object AppDataPath {
        // NBA
        const val TEAM_THEME_JSON_PATH = "/team_theme.json"
        const val TEAM_BANNER_URL = "https://hongwei-test1.top/resize/1080/sports_v2/banner/{team}.jpg"
        const val TEAM_BACKGROUND_URL = "https://hongwei-test1.top/resize/1080/sports_v2/background/{team}.jpg"

        // Soccer
        const val SOCCER_DATA_PATH = "soccer_v1"
        const val SOCCER_LEAGUE_JSON_PATH = "/soccer_league.json"
    }

    object LogoPath {
        const val NBA = "logo/nba/"
        const val SOCCER = "logo/soccer/"

        const val URL_BASE = "https://hongwei-test1.top/resize/480/sports_v2/"
        const val LOGO_PLACEHOLDER = "no_logo.png"
    }

    object TimeZone {
        const val UTC = "UTC"
        const val SYDNEY = "Australia/Sydney"
    }
}