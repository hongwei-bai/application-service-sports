package com.hongwei.service.scheduler

import com.hongwei.constants.Constants.TimeZone.SYDNEY
import com.hongwei.controller.StatHubController
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*

@Component
class ScheduledTasks {
    private val logger: Logger = LogManager.getLogger(ScheduledTasks::class.java)

    @Autowired
    private lateinit var statHubController: StatHubController

    // 60 mins : 60 min x 60 s x 1000 ms = 1,800,000, For copy:3600000
    @Scheduled(fixedRate = 3600000)
    fun reportCurrentTime() {
        val sydTime = Calendar.getInstance(TimeZone.getTimeZone(SYDNEY))
        val hour = sydTime.get(Calendar.HOUR_OF_DAY)
        if (HoursUpdate.contains(hour)) {
            statHubController.generateEspnStanding()

            Thread {
                Thread.sleep(1000 * 30)
                statHubController.generateEspnAllTeamSchedule()
            }.start()
        }
    }

    companion object {
        val HoursUpdate = listOf(4, 16)
    }
}