package com.hongwei.model.nba

data class PlayOffSource(
        val dataVersion: Long,
        val western: ConferencePlayOffSource,
        val eastern: ConferencePlayOffSource,
        val final: PlayOffRoundSource
)

data class ConferencePlayOffSource(
        val teams: List<String>,
        val rounds: List<PlayOffRoundSource>
)

data class PlayOffRoundSource(
        val conference: String,
        val round: Int,
        val leg: Int,
        val teamHigherRank: PlayOffTeamSource,
        val teamLowerRank: PlayOffTeamSource,
        val teamHigherRankWins: Int,
        val teamLowerRankWins: PlayOffTeamSource,
        val eventsInSeries: List<PlayOffEventSource>?
)

data class PlayOffTeamSource(
        val rank: Int,
        val teamAbbr: String,
        val teamName: String
)

data class PlayOffEventSource(
        val unixTimeStamp: Long,
        val homeForHigherRankTeam: Boolean,
        val result: PlayOffResultSource? = null
)

data class PlayOffResultSource(
        val higherRankTeamWinLossSymbol: String,
        val higherRankTeamScore: Int,
        val lowerRankTeamScore: Int
)