package com.hongwei.model.jpa.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hongwei.model.nba.PlayOffSeries
import java.lang.reflect.Type
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class NbaPlayOffSeriesConverter : AttributeConverter<PlayOffSeries?, String?> {
    override fun convertToDatabaseColumn(obj: PlayOffSeries?): String? {
        return obj?.let { Gson().toJson(obj) }
    }

    override fun convertToEntityAttribute(string: String?): PlayOffSeries? {
        val type: Type = object : TypeToken<PlayOffSeries>() {}.type
        return if (string != null) Gson().fromJson(string, type) else null
    }
}