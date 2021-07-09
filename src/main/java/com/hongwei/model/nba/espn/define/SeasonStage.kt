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

    companion object {
        fun parsePostSeasonTypeFromTitle(title: String) =
                if (title.contains("Finals")) {
                    when {
                        title.contains("Conference") -> {
                            ConferenceFinal
                        }
                        title.contains("NBA") -> {
                            GrandFinal
                        }
                        else -> {
                            null
                        }
                    }
                } else if (title.contains("Semifinals")) {
                    Round2
                } else if (title.contains("First") && title.contains("Round")) {
                    Round1
                } else null
    }
}