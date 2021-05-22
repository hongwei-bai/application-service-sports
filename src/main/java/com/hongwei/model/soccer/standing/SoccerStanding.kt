package com.hongwei.model.soccer.standing

data class SoccerLeagueOverall(
        val dataVersion: String,
        val standing: List<SoccerStandingStats>
)

data class SoccerStandingStats(
        val teamId: Int,
        val teamAbbr: String,
        val team: String,
        val path: String,
        val logoEspn: String,
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
        val pointDeductions: Int,
        val pointsPerGame: Int,
        val overallRecord: Int
)
