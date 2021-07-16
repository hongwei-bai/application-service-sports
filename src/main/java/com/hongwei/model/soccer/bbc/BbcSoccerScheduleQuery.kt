package com.hongwei.model.soccer.bbc

class BbcSoccerScheduleQuery(var year: Int, var monthNumber: Int) {
    private var path = "https://www.bbc.com/sport/football/champions-league/scores-fixtures/$year-${monthNumber.toString().padStart(2, '0')}"

    fun build(): String {
        return path
    }
}