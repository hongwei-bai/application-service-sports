package com.hongwei.model.jpa.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hongwei.model.nba.Event
import java.lang.reflect.Type
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class NbaScheduleEventListConverter : AttributeConverter<List<Event>, String?> {
    override fun convertToDatabaseColumn(stringList: List<Event>): String? {
        return Gson().toJson(stringList)
    }

    override fun convertToEntityAttribute(string: String?): List<Event> {
        val listType: Type = object : TypeToken<List<Event>>() {}.type
        return if (string != null) Gson().fromJson(string, listType) else emptyList()
    }
}