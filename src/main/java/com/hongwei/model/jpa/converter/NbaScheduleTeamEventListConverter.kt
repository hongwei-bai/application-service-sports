package com.hongwei.model.jpa.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hongwei.model.nba.TeamEvent
import java.lang.reflect.Type
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class NbaScheduleTeamEventListConverter : AttributeConverter<List<TeamEvent>, String?> {
    override fun convertToDatabaseColumn(stringList: List<TeamEvent>): String? {
        return Gson().toJson(stringList)
    }

    override fun convertToEntityAttribute(string: String?): List<TeamEvent> {
        val listType: Type = object : TypeToken<List<TeamEvent>>() {}.type
        return if (string != null) Gson().fromJson(string, listType) else emptyList()
    }
}