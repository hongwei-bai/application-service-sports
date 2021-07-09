package com.hongwei.model.nba

import java.lang.Exception

enum class EventType {
    PreSeason,
    Season,
    PlayIn,
    PlayOffRound1,
    PlayOffRound2,
    PlayOffConferenceFinal,
    PlayOffGrandFinal;

    companion object {
        fun isPlayOff(title: String): Boolean = try {
            when (valueOf(title)) {
                PlayOffRound1, PlayOffRound2, PlayOffConferenceFinal, PlayOffGrandFinal -> true
                else -> false
            }
        } catch (e: Exception) {
            false
        }
    }
}