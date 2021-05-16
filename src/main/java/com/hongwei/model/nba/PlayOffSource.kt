package com.hongwei.model.nba

data class PlayOffSourceRoot(
        val dataVersion: Long,
        val seasonOngoing: Boolean,
        val playInOngoing: Boolean,
        val playOffOngoing: Boolean,
        val playIn: PlayInSource,
        val playOff: PlayOffSource
) {
    fun map() = PlayOffResponseBody(
            dataVersion = dataVersion,
            seasonOngoing = seasonOngoing,
            playInOngoing = playInOngoing,
            playOffOngoing = playOffOngoing,
            playIn = playIn.map(),
            playOff = playOff.map()
    )
}

data class PlayInSource(
        val western: PlayInConferenceSource,
        val eastern: PlayInConferenceSource
) {
    fun map() = PlayIn(
            western = western.map(),
            eastern = eastern.map()
    )
}

data class PlayOffSource(
        val western: PlayOffConferenceSource,
        val eastern: PlayOffConferenceSource,
        val grandFinal: PlayOffGrandFinalSeriesSource
) {
    fun map() = PlayOff(
            western = western.map(),
            eastern = eastern.map(),
            grandFinal = grandFinal.map()
    )
}

data class PlayInConferenceSource(
        val winnerOf78: String,
        val winnerOf910: String,
        val lastWinner: String
) {
    fun map() = PlayInConference(
            winnerOf78 = winnerOf78,
            winnerOf910 = winnerOf910,
            lastWinner = lastWinner
    )
}

data class PlayOffConferenceSource(
        val round1: PlayOffRound1Source,
        val round2: PlayOffRound2Source,
        val conferenceFinal: PlayOffSeriesSource
) {
    fun map() = PlayOffConference(
            round1 = round1.map(),
            round2 = round2.map(),
            conferenceFinal = conferenceFinal.map()
    )
}

data class PlayOffRound1Source(
        val series18: PlayOffSeriesRound1Source,
        val series45: PlayOffSeriesRound1Source,
        val series36: PlayOffSeriesRound1Source,
        val series27: PlayOffSeriesRound1Source
) {
    fun map() = PlayOffRound1(
            series18 = series18.map(),
            series45 = series45.map(),
            series36 = series36.map(),
            series27 = series27.map()
    )
}

data class PlayOffRound2Source(
        val seriesUpper: PlayOffSeriesSource,
        val seriesLower: PlayOffSeriesSource
) {
    fun map() = PlayOffRound2(
            seriesUpper = seriesUpper.map(),
            seriesLower = seriesLower.map()
    )
}

data class PlayOffSeriesRound1Source(
        val scoreHighRank: Int,
        val scoreLowRank: Int,
        val winner: String
) {
    fun map() = PlayOffSeriesRound1(
            scoreHighRank = scoreHighRank,
            scoreLowRank = scoreLowRank,
            winner = winner
    )
}

data class PlayOffSeriesSource(
        val teamFromUpper: String,
        val teamFromLower: String,
        val scoreUpperWinner: Int,
        val scoreLowerWinner: Int,
        val winner: String
) {
    fun map() = PlayOffSeries(
            teamFromUpper = teamFromUpper,
            teamFromLower = teamFromLower,
            scoreUpperWinner = scoreUpperWinner,
            scoreLowerWinner = scoreLowerWinner,
            winner = winner
    )
}

data class PlayOffGrandFinalSeriesSource(
        val teamFromWestern: String,
        val teamFromEastern: String,
        val scoreWesternWinner: Int,
        val scoreEasternWinner: Int,
        val winner: String
) {
    fun map() = PlayOffGrandFinalSeries(
            teamFromWestern = teamFromWestern,
            teamFromEastern = teamFromEastern,
            scoreWesternWinner = scoreWesternWinner,
            scoreEasternWinner = scoreEasternWinner,
            winner = winner
    )
}