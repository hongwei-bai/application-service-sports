package com.hongwei.model.nba

data class PostSeason(
        val dataVersion: Long,
        val currentStage: String,
        val westernPlayInEventRound1: List<PlayInEvent?>,
        val westernPlayInEventRound2: PlayInEvent?,
        val easternPlayInEventRound1: List<PlayInEvent?>,
        val easternPlayInEventRound2: PlayInEvent?,
        val westernRound1Series: List<PlayOffSeries?>,
        val easternRound1Series: List<PlayOffSeries?>,
        val westernRound2Series: List<PlayOffSeries?>,
        val easternRound2Series: List<PlayOffSeries?>,
        val westernConferenceFinal: PlayOffSeries?,
        val easternConferenceFinal: PlayOffSeries?,
        val final: PlayOffSeries?
)

data class PlayOffSeries(
        val team1: PostSeasonTeam,
        val team2: PostSeasonTeam,
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