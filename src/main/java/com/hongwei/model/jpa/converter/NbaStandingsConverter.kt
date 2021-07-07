package com.hongwei.model.jpa.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hongwei.model.nba.TeamStandingData
import java.lang.reflect.Type
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class NbaStandingsConverter : AttributeConverter<List<TeamStandingData>?, String?> {
    override fun convertToDatabaseColumn(stringList: List<TeamStandingData>?): String? {
        return if (stringList != null) Gson().toJson(stringList) else ""
    }

    override fun convertToEntityAttribute(string: String?): List<TeamStandingData>? {
        val listType: Type = object : TypeToken<List<TeamStandingData>?>() {}.type
        return if (string != null) Gson().fromJson(string, listType) else emptyList()
    }
}