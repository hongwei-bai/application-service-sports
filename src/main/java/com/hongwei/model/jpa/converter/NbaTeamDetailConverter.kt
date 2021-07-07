package com.hongwei.model.jpa.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hongwei.model.nba.TeamDetail
import java.lang.reflect.Type
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class NbaTeamDetailConverter : AttributeConverter<TeamDetail?, String?> {
    override fun convertToDatabaseColumn(stringList: TeamDetail?): String? {
        return if (stringList != null) Gson().toJson(stringList) else ""
    }

    override fun convertToEntityAttribute(string: String?): TeamDetail? {
        val listType: Type = object : TypeToken<TeamDetail?>() {}.type
        return if (string != null) Gson().fromJson(string, listType) else null
    }
}