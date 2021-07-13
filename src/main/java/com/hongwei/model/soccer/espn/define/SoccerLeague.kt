package com.hongwei.model.soccer.espn.define

enum class SoccerLeague(val path: String) {
    ItalianSerieA("ITA.1"),
    ItalianCoppaItalia("ITA.COPPA_ITALIA"),
    ItalianSuperCoppa("ITA.SUPER_CUP"),

    UEFAChampionsLeague("UEFA.CHAMPIONS"),
    UEFAEuropaLeague("UEFA.EUROPA"),
    UEFAEuropaLeagueQualifying("UEFA.EUROPA_QUAL"),
    ClubFriendly("CLUB.FRIENDLY");
}