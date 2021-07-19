package com.hongwei.model.nba.espn

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties
data class TransactionSource(
	val date: String,
	val description: String,
	val team: TransactionTeamSource
)

@JsonIgnoreProperties
data class TransactionTeamSource(
	val id: Int,
	val location: String,
	val name: String,
	val abbreviation: String,
	val displayName: String,
	val color: String,
	val alternateColor: String
)