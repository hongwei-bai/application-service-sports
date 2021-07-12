package com.hongwei.model.jpa

import com.hongwei.model.jpa.converter.NbaPlayInEventListConverter
import com.hongwei.model.nba.PlayInEvent
import javax.persistence.*


@Entity
data class NbaPostSeasonArchivedEntity(
        @Id @GeneratedValue
        private var id: Long = 0L,

        @Lob @Convert(converter = NbaPlayInEventListConverter::class) @Column(nullable = true)
        val westernPlayInEvents: List<PlayInEvent?> = emptyList(),
        @Lob @Convert(converter = NbaPlayInEventListConverter::class) @Column(nullable = true)
        val easternPlayInEvents: List<PlayInEvent?> = emptyList()
)

