package com.hongwei.model.jpa.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hongwei.model.soccer.SoccerTeamEvent
import java.lang.reflect.Type
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class SoccerTeamScheduleConverter : AttributeConverter<List<SoccerTeamEvent>, String?> {
    override fun convertToDatabaseColumn(stringList: List<SoccerTeamEvent>): String? {
        return Gson().toJson(stringList)
    }

    override fun convertToEntityAttribute(string: String?): List<SoccerTeamEvent> {
        val listType: Type = object : TypeToken<List<SoccerTeamEvent>>() {}.type
        return if (string != null) Gson().fromJson(string, listType) else emptyList()
    }
}