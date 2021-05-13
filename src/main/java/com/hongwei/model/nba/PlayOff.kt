package com.hongwei.model.nba

data class PlayOff(
        val dataVersion: Long,
        val western: ConferencePlayOff,
        val eastern: ConferencePlayOff,
        val final: ConferencePlayOff
)

data class ConferencePlayOff(
        val teams: List<PlayOffTeam>,
        val rounds: List<PlayOffRound>
)

data class PlayOffRound(
        val conference: String,
        val round: Int,
        val leg: Int,
        val teamHigherRank: PlayOffTeam,
        val teamLowerRank: PlayOffTeam,
        val teamHigherRankWins: Int,
        val teamLowerRankWins: PlayOffTeam,
        val eventsInSeries: List<PlayOffEvent>?
)

data class PlayOffTeam(
        val rank: Int,
        val teamAbbr: String,
        val teamName: String
)

data class PlayOffEvent(
        val unixTimeStamp: Long,
        val homeForHigherRankTeam: Boolean,
        val result: PlayOffResult? = null
)

data class PlayOffResult(
        val higherRankTeamWinLossSymbol: String,
        val higherRankTeamScore: Int,
        val lowerRankTeamScore: Int
)