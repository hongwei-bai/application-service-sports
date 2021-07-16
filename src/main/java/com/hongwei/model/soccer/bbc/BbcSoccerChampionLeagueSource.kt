package com.hongwei.model.soccer.bbc

data class BbcSoccerChampionLeagueSource(
        val matchData: List<BbcSoccerMatchDataSource> = emptyList()
)

data class BbcSoccerMatchDataSource(
        val tournamentMeta: BbcSoccerTournamentMetaSource,
        val tournamentDatesWithEvents: Map<String?, List<BbcSoccerEventWrapperSource>?>?
)

data class BbcSoccerTournamentMetaSource(
        val tournamentSlug: String,
        val tournamentName: BbcSoccerNameDataSource
)

data class BbcSoccerNameDataSource(
        val first: String,
        val full: String,
        val abbreviation: String,
        val videCode: String?,
        val last: String?
)

// Any in between

data class BbcSoccerEventWrapperSource(
        val round: BbcSoccerEventRoundSource,
        val events: List<BbcSoccerEventSource>
)

data class BbcSoccerEventRoundSource(
        val key: String,
        val name: BbcSoccerNameDataSource
)

data class BbcSoccerEventSource(
        val eventKey: String,
        val startTime: String,
        val isTBC: Boolean?,
        val minutesElapsed: Int?,
        val minutesIntoAddedTime: Int?,
        val eventStatus: String,
        val eventStatusNote: String,
        val eventOutcomeType: String?,
        val eventType: String,
        val seriesWinner: String?,
        val homeTeam: BbcSoccerTeamSource,
        val awayTeam: BbcSoccerTeamSource,
        val eventProgress: BbcSoccerEventProgressSource,
        val venue: BbcSoccerVenueSource
)

data class BbcSoccerTeamSource(
        val key: String,
        val scores: BbcSoccerTeamScoreSource,
        val formation: String?,
        val eventOutcome: String?,
        val name: BbcSoccerNameDataSource
)

data class BbcSoccerTeamScoreSource(
        val score: Int?,
        val halfTime: Int?,
        val fullTime: Int?,
        val extraTime: Int?,
        val shootout: Int?,
        val aggregate: Int?,
        val aggregateGoalsAway: Int?
)

data class BbcSoccerVenueSource(
        val name: BbcSoccerNameDataSource,
        val homeCountry: String?
)

data class BbcSoccerEventProgressSource(
        val period: String,
        val status: String
)