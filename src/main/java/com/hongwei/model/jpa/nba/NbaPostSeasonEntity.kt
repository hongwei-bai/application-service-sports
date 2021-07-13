package com.hongwei.model.jpa.nba

import com.hongwei.model.jpa.converter.NbaPlayInEventListConverter
import com.hongwei.model.jpa.converter.NbaPlayOffSeriesConverter
import com.hongwei.model.jpa.converter.NbaPlayOffSeriesListConverter
import com.hongwei.model.nba.PlayInEvent
import com.hongwei.model.nba.PlayOffSeries
import javax.persistence.*


@Entity
data class NbaPostSeasonEntity(
        @Id @GeneratedValue
        private var id: Long = 0L,
        @Column(nullable = false)
        var dataVersion: Long = 0L,

        @Lob @Convert(converter = NbaPlayInEventListConverter::class) @Column(nullable = true)
        val westernPlayInEvents: List<PlayInEvent?> = emptyList(),
        @Lob @Convert(converter = NbaPlayInEventListConverter::class) @Column(nullable = true)
        val easternPlayInEvents: List<PlayInEvent?> = emptyList(),

        @Lob @Convert(converter = NbaPlayOffSeriesListConverter::class) @Column(nullable = true)
        val westernRound1Series: List<PlayOffSeries?> = emptyList(),
        @Lob @Convert(converter = NbaPlayOffSeriesListConverter::class) @Column(nullable = true)
        val easternRound1Series: List<PlayOffSeries?> = emptyList(),
        @Lob @Convert(converter = NbaPlayOffSeriesListConverter::class) @Column(nullable = true)
        val westernRound2Series: List<PlayOffSeries?> = emptyList(),
        @Lob @Convert(converter = NbaPlayOffSeriesListConverter::class) @Column(nullable = true)
        val easternRound2Series: List<PlayOffSeries?> = emptyList(),
        @Lob @Convert(converter = NbaPlayOffSeriesConverter::class) @Column(nullable = true)
        val westernConferenceFinal: PlayOffSeries? = null,
        @Lob @Convert(converter = NbaPlayOffSeriesConverter::class) @Column(nullable = true)
        val easternConferenceFinal: PlayOffSeries? = null,
        @Lob @Convert(converter = NbaPlayOffSeriesConverter::class) @Column(nullable = true)
        val final: PlayOffSeries? = null
) {
    override fun equals(other: Any?): Boolean = (other as? NbaPostSeasonEntity)?.let {
        westernPlayInEvents == it.westernPlayInEvents &&
                easternPlayInEvents == it.easternPlayInEvents &&
                easternPlayInEvents == it.easternPlayInEvents &&
                westernRound1Series == it.westernRound1Series &&
                easternRound1Series == it.easternRound1Series &&
                westernRound2Series == it.westernRound2Series &&
                easternRound2Series == it.easternRound2Series &&
                westernConferenceFinal == it.westernConferenceFinal &&
                easternConferenceFinal == it.easternConferenceFinal &&
                final == it.final
    } ?: false
}

