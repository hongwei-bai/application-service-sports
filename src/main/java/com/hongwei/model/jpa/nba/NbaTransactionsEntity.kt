package com.hongwei.model.jpa.nba

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.hongwei.model.jpa.converter.NbaTransactionListConverter
import javax.persistence.*

@Entity
data class NbaTransactionsEntity(
        @Id @Column(nullable = false)
        var dataVersion: Long = 0L,

        @Lob @Convert(converter = NbaTransactionListConverter::class) @Column(nullable = false)
        val transactions: List<Transaction> = emptyList()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NbaTransactionsEntity

        if (transactions != other.transactions) return false

        return true
    }

    override fun hashCode(): Int {
        return transactions.hashCode()
    }
}

@JsonIgnoreProperties
data class Transaction(
        val unixTimeStamp: Long,
        val description: String,
        val teamAbbr: String,
        var teamDisplayName: String,
        var teamLogo: String
)