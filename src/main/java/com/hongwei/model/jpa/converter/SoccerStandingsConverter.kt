package com.hongwei.model.jpa.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hongwei.model.soccer.standing.SoccerStandingStats
import java.lang.reflect.Type
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class SoccerStandingsConverter : AttributeConverter<List<SoccerStandingStats>, String?> {
    override fun convertToDatabaseColumn(stringList: List<SoccerStandingStats>): String? {
        return Gson().toJson(stringList)
    }

    override fun convertToEntityAttribute(string: String?): List<SoccerStandingStats> {
        val listType: Type = object : TypeToken<List<SoccerStandingStats>>() {}.type
        return if (string != null) Gson().fromJson(string, listType) else emptyList()
    }
}