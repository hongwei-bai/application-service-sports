package com.hongwei.model.soccer

data class SoccerStanding(
        val dataVersion: Long,
        val league: String,
        val leagueTitle: String,
        val standings: List<SoccerStandingStats>
)

data class SoccerStandingStats(
        val teamId: Long,
        val teamAbbr: String,
        val displayName: String,
        val shortDisplayName: String,
        val logo: String?,
        val wins: Int,
        val losses: Int,
        val draws: Int,
        val gamePlayed: Int,
        val goalsFor: Int,
        val goalsAgainst: Int,
        val points: Int,
        val rankChange: Int,
        val rank: Int,
        val goalDifference: Int,
        val pointDeductions: String,
        val pointsPerGame: Int
)
