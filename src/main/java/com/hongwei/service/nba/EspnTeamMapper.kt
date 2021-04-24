package com.hongwei.service.nba

import org.springframework.stereotype.Service
import java.util.*

@Service
class EspnTeamMapper {
    fun teamShortMapToLegacy(teamShort: String): String = when (teamShort) {
        "BKN" -> "njn"
        "NY" -> "nyk"
        "GS" -> "gsw"
        "SA" -> "sas"
        else -> teamShort.toLowerCase(Locale.ROOT)
    }
}