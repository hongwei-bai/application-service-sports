package com.hongwei.service.nba

import com.google.gson.Gson
import com.hongwei.constants.AppDataConfigurations
import com.hongwei.model.nba.StandingSource
import com.hongwei.util.TimeStampUtil
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileWriter
import java.io.IOException

@Service
class JsonWriterService {
    private val logger: Logger = LogManager.getLogger(JsonWriterService::class.java)

    @Autowired
    private lateinit var appDataConfigurations: AppDataConfigurations

    fun writeStanding(jsonObj: StandingSource) {
        writeJSONObject(jsonObj, "${appDataConfigurations.nbaPath}/standing.json")
        writeJSONObject(jsonObj, "${appDataConfigurations.nbaPath}/standing_${TimeStampUtil.getTimeVersionWithHour()}.json")
    }

    fun writeTeamSchedule(team: String, jsonString: String) {
        writeJsonString(jsonString, "${appDataConfigurations.nbaPath}/schedule_$team.json")
    }

    private fun writeJSONObject(obj: Any, file: String) {
        writeJsonString(Gson().toJson(obj), file)
    }

    private fun writeJsonString(jsonString: String, file: String) {
        var fileWriter: FileWriter? = null
        try {
            fileWriter = FileWriter(file)
            fileWriter.write(jsonString)
        } catch (e: IOException) {
            throw e
        } finally {
            fileWriter?.flush()
            fileWriter?.close()
        }
    }

    fun archiveScheduleData(folderName: String, team: String? = null) {
        val root = File(appDataConfigurations.nbaPath)
        val backupFolder = File("${appDataConfigurations.nbaPath}/backup/$folderName")
        root.listFiles().forEach { file ->
            if (file.isFile && file.name.startsWith("schedule_") && file.name.endsWith(".json")) {
                if (team == null || file.name == "schedule_$team.json") {
                    file.renameTo(File(backupFolder, file.name))
                }
            }
        }
    }
}