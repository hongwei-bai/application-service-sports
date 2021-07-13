package com.hongwei.model.jpa.nba

import com.hongwei.model.jpa.converter.NbaPlayInEventListConverter
import com.hongwei.model.nba.PlayInEvent
import javax.persistence.*


@Entity
data class NbaPlayInEntity(
        @Id @GeneratedValue
        private var id: Long = 0L,
        @Column(nullable = false)
        var dataVersion: Long = 0L,

        @Lob @Convert(converter = NbaPlayInEventListConverter::class) @Column(nullable = true)
        val westernPlayInEvents: List<PlayInEvent?> = emptyList(),
        @Lob @Convert(converter = NbaPlayInEventListConverter::class) @Column(nullable = true)
        val easternPlayInEvents: List<PlayInEvent?> = emptyList()
) {
    override fun equals(other: Any?): Boolean = (other as? NbaPlayInEntity)?.let {
        westernPlayInEvents == it.westernPlayInEvents && easternPlayInEvents == it.easternPlayInEvents
    } ?: false
}

