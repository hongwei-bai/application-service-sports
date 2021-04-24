package com.hongwei.model.nba

import java.util.*

object EspnTeamMapper {
    fun teamShortMapToLegacy(teamShort: String): String = when (teamShort.toUpperCase(Locale.ROOT)) {
        "BKN" -> "njn"
        "NY" -> "nyk"
        "GS" -> "gsw"
        "SA" -> "sas"
        else -> teamShort.toLowerCase(Locale.ROOT)
    }

    fun mapLegacyTeamShort(legacyTeamShort: String): String = when (legacyTeamShort.toLowerCase(Locale.ROOT)) {
        "njn" -> "bkn"
        "nyk" -> "ny"
        "gsw" -> "gs"
        "sas" -> "sa"
        else -> legacyTeamShort.toLowerCase(Locale.ROOT)
    }
}