package com.hongwei.model.jpa

import com.hongwei.model.jpa.converter.NbaScheduleEventListConverter
import com.hongwei.model.nba.Event
import javax.persistence.*


@Entity
data class NbaScheduleEntity(
        @Id @GeneratedValue
        private var id: Long = 0L,

        @Column(nullable = false)
        var dataVersion: Long = 0L,

        @Lob
        @Convert(converter = NbaScheduleEventListConverter::class)
        @Column(nullable = false)
        var events: List<Event> = emptyList()
)

