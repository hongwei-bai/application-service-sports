package com.hongwei.model.nba

data class PostSeason(
        val dataVersion: Long,
        val currentStage: String,
        val westernPlayInEventRound1: List<PlayInEvent?>,
        val westernPlayInEventRound2: PlayInEvent?,
        val easternPlayInEventRound1: List<PlayInEvent?>,
        val easternPlayInEventRound2: PlayInEvent?,
        val westernRound1Series: List<PlayOffSeries?> = emptyList(),
        val easternRound1Series: List<PlayOffSeries?> = emptyList(),
        val westernRound2Series: List<PlayOffSeries?> = emptyList(),
        val easternRound2Series: List<PlayOffSeries?> = emptyList(),
        val westernConferenceFinal: PlayOffSeries? = null,
        val easternConferenceFinal: PlayOffSeries? = null,
        val final: PlayOffSeries? = null
)

data class PlayOffSeries(
        val team1: PostSeasonTeam,
        val team2: PostSeasonTeam,
        val team1Wins: Int,
        val team2Wins: Int,
        val events: List<Event?>
)

data class PlayInEvent(
        val homeTeam: PostSeasonTeam,
        val guestTeam: PostSeasonTeam,
        val result: Result?
)

data class PostSeasonTeam(
        val teamAbbr: String,
        val displayName: String,
        val logo: String,
        val location: String,
        val rank: Int,
        val seed: Int,
        val recordSummaryWin: Int,
        val recordSummaryLose: Int,
        var isSurviveToPlayOff: Boolean,
        var isSurvive: Boolean = true
)