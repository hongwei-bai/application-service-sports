package com.hongwei.model.jpa.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hongwei.model.nba.PlayOffSeries
import java.lang.reflect.Type
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class NbaPlayOffSeriesListConverter : AttributeConverter<List<PlayOffSeries?>, String?> {
    override fun convertToDatabaseColumn(stringList: List<PlayOffSeries?>): String? {
        return stringList.let { Gson().toJson(stringList) }
    }

    override fun convertToEntityAttribute(string: String?): List<PlayOffSeries?> {
        val listType: Type = object : TypeToken<List<PlayOffSeries?>>() {}.type
        return if (string != null) Gson().fromJson(string, listType) else emptyList()
    }
}