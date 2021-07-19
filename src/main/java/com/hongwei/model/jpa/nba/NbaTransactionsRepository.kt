package com.hongwei.model.jpa.nba

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface NbaTransactionsRepository : JpaRepository<NbaTransactionsEntity?, String?> {
	@Query("from NbaTransactionsEntity entity")
	fun findTransactions(): NbaTransactionsEntity?
}