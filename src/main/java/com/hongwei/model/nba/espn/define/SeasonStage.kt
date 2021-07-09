package com.hongwei.model.nba.espn.define

enum class SeasonStage(val title: String) {
    // PreSeason
    PreSeasonStandard("Standard"),

    // Regular
    RegularStandard("Standard"),

    // PlayIn
    PlayInStandard("Standard"),

    // Post
    GrandFinal("NBA Finals"),
    Round1("Conference First Round"),
    Round2("Conference Semifinals"),
    ConferenceFinal("Conference Finals");
}