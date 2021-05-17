package com.hongwei.model.nba

data class PlayOffResponseBody(
        val dataVersion: Long,
        val seasonOngoing: Boolean,
        val playInOngoing: Boolean,
        val playOffOngoing: Boolean,
        val playIn: PlayIn,
        val playOff: PlayOff
)

data class PlayIn(
        val western: PlayInConference,
        val eastern: PlayInConference
)

data class PlayOff(
        val western: PlayOffConference,
        val eastern: PlayOffConference,
        val grandFinal: PlayOffGrandFinalSeries
)

data class PlayInConference(
        val winnerOf78: String,
        val loserOf78: String,
        val winnerOf910: String,
        val loserOf910: String,
        val lastWinner: String
)

data class PlayOffConference(
        val round1: PlayOffRound1,
        val round2: PlayOffRound2,
        val conferenceFinal: PlayOffSeries
)

data class PlayOffRound1(
        val series18: PlayOffSeriesRound1,
        val series45: PlayOffSeriesRound1,
        val series36: PlayOffSeriesRound1,
        val series27: PlayOffSeriesRound1
)

data class PlayOffRound2(
        val seriesUpper: PlayOffSeries,
        val seriesLower: PlayOffSeries
)

data class PlayOffSeriesRound1(
        val scoreHighRank: Int,
        val scoreLowRank: Int,
        val winner: String
)

data class PlayOffSeries(
        val teamFromUpper: String,
        val teamFromLower: String,
        val scoreUpperWinner: Int,
        val scoreLowerWinner: Int,
        val winner: String
)

data class PlayOffGrandFinalSeries(
        val teamFromWestern: String,
        val teamFromEastern: String,
        val scoreWesternWinner: Int,
        val scoreEasternWinner: Int,
        val winner: String
)