package com.hongwei.util

import java.util.*

object DateTimeUtil {
    private const val MONTH_BASE = 12

    fun getCurrentYearMonth(): Pair<Int, Int> {
        val date = Calendar.getInstance()
        val year = date.get(Calendar.YEAR)
        val month = date.get(Calendar.MONTH) + 1
        return Pair(year, month)
    }

    fun getNextYearMonth(monthOffset: Int): Pair<Int, Int> {
        val date = Calendar.getInstance()
        val year = date.get(Calendar.YEAR)
        val month = date.get(Calendar.MONTH)
        val yearShift = monthOffset / MONTH_BASE
        val monthShiftImpl = monthOffset % MONTH_BASE

        val newMonth = (month + monthShiftImpl) % MONTH_BASE + 1
        val newYear = year + yearShift + ((month + monthShiftImpl) / MONTH_BASE)
        return Pair(newYear, newMonth)
    }
}