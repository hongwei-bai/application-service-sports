package com.hongwei.model.nba

data class Standing(
        val dataVersion: Long,
        val western: List<TeamStanding>,
        val eastern: List<TeamStanding>
)

data class TeamStanding(
        val rank: Int,
        val teamAbbr: String,
        val teamName: String,
        val teamLogo: String,
        val teamLocation: String,
        val wins: Int,
        val losses: Int,
        val pct: Double,
        val gamesBack: Double,
        val homeRecords: Pair<Int, Int>,
        val awayRecords: Pair<Int, Int>,
        val divisionRecords: Pair<Int, Int>,
        val conferenceRecords: Pair<Int, Int>,
        val pointsPerGame: Double,
        val opponentPointsPerGame: Double,
        val avePointsDiff: Double,
        val currentStreak: Pair<String, Int>,
        val last10Records: Pair<Int, Int>
)