package com.hongwei.model.jpa

import com.hongwei.model.jpa.converter.*
import com.hongwei.model.nba.PlayInEvent
import com.hongwei.model.nba.PlayOffSeries
import javax.persistence.*


@Entity
data class NbaPostSeasonArchivedEntity(
        @Id @GeneratedValue
        private var id: Long = 0L,

        @Lob @Convert(converter = NbaPlayInEventListConverter::class) @Column(nullable = true)
        val westernPlayInEvents: List<PlayInEvent?> = emptyList(),
        @Lob @Convert(converter = NbaPlayInEventListConverter::class) @Column(nullable = true)
        val easternPlayInEvents: List<PlayInEvent?> = emptyList(),

        @Lob @Convert(converter = NbaPlayOffSeriesListConverter::class) @Column(nullable = true)
        val westernRound1Series: List<PlayOffSeries>? = emptyList(),
        @Lob @Convert(converter = NbaPlayOffSeriesListConverter::class) @Column(nullable = true)
        val easternRound1Series: List<PlayOffSeries>? = emptyList(),
        @Lob @Convert(converter = NbaPlayOffSeriesListConverter::class) @Column(nullable = true)
        val westernRound2Series: List<PlayOffSeries>? = emptyList(),
        @Lob @Convert(converter = NbaPlayOffSeriesListConverter::class) @Column(nullable = true)
        val easternRound2Series: List<PlayOffSeries>? = emptyList(),
        @Lob @Convert(converter = NbaPlayOffSeriesConverter::class) @Column(nullable = true)
        val westernConferenceFinal: PlayOffSeries? = null,
        @Lob @Convert(converter = NbaPlayOffSeriesConverter::class) @Column(nullable = true)
        val easternConferenceFinal: PlayOffSeries? = null,
        @Lob @Convert(converter = NbaPlayOffSeriesConverter::class) @Column(nullable = true)
        val final: PlayOffSeries? = null
)

