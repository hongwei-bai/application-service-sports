package com.hongwei.model.jpa.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hongwei.model.jpa.nba.Transaction
import java.lang.reflect.Type
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class NbaTransactionListConverter : AttributeConverter<List<Transaction?>, String?> {
    override fun convertToDatabaseColumn(stringList: List<Transaction?>): String? {
        return stringList.let { Gson().toJson(stringList) }
    }

    override fun convertToEntityAttribute(string: String?): List<Transaction?> {
        val listType: Type = object : TypeToken<List<Transaction>?>() {}.type
        return if (string != null) Gson().fromJson(string, listType) else emptyList()
    }
}