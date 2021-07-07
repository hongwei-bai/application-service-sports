package com.hongwei.model.nba

data class Schedule(
        val dataVersion: Long = 0,
        val events: List<Event>
)