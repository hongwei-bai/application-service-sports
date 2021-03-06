package com.hongwei.model.jpa.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hongwei.model.nba.TeamStanding
import java.lang.reflect.Type
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class NbaStandingsConverter : AttributeConverter<List<TeamStanding>, String?> {
    override fun convertToDatabaseColumn(stringList: List<TeamStanding>): String? {
        return Gson().toJson(stringList)
    }

    override fun convertToEntityAttribute(string: String?): List<TeamStanding> {
        val listType: Type = object : TypeToken<List<TeamStanding>>() {}.type
        return if (string != null) Gson().fromJson(string, listType) else emptyList()
    }
}