package com.hongwei.model.nba.espn.mapper

import com.hongwei.model.jpa.nba.NbaTeamDetailEntity
import com.hongwei.model.jpa.nba.NbaTransactionsEntity
import com.hongwei.model.jpa.nba.Transaction
import com.hongwei.model.nba.espn.TransactionSource
import com.hongwei.util.EspnDateTimeParseUtil
import com.hongwei.util.TimeStampUtil

object NbaTransactionsMapper {
    fun map(sources: List<TransactionSource>, teamDetailMap: Map<String, NbaTeamDetailEntity>): NbaTransactionsEntity =
            NbaTransactionsEntity(
                    dataVersion = TimeStampUtil.getTimeVersionWithMinute(),
                    transactions = sources.mapNotNull { sourceTransaction ->
                        EspnDateTimeParseUtil.parseDate(sourceTransaction.date)?.time?.let { timeStamp ->
                            val teamDetail = teamDetailMap[sourceTransaction.team.abbreviation.toLowerCase()]
                            Transaction(
                                    unixTimeStamp = timeStamp,
                                    description = sourceTransaction.description,
                                    teamAbbr = sourceTransaction.team.abbreviation,
                                    teamDisplayName = teamDetail?.displayName ?: "",
                                    teamLogo = teamDetail?.logo ?: ""
                            )
                        }
                    }
            )
}