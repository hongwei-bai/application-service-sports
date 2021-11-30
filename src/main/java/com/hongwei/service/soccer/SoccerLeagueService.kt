package com.hongwei.service.soccer

import com.hongwei.constants.NoContent
import com.hongwei.constants.ResetContent
import com.hongwei.model.jpa.nba.NbaTeamScheduleRepository
import com.hongwei.model.jpa.soccer.SoccerStandingRepository
import com.hongwei.model.jpa.soccer.SoccerTeamScheduleRepository
import com.hongwei.model.soccer.espn.mapper.SoccerStandingMapper
import com.hongwei.model.soccer.SoccerStanding
import com.hongwei.model.soccer.SoccerTeamSchedule
import com.hongwei.model.soccer.espn.mapper.SoccerTeamScheduleMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SoccerLeagueService {
    @Autowired
    private lateinit var soccerStandingRepository: SoccerStandingRepository

    @Autowired
    private lateinit var soccerTeamScheduleRepository: SoccerTeamScheduleRepository

    fun getStandings(league: String, dataVersion: Long): SoccerStanding? {
        val standingEntityDb = soccerStandingRepository.findStandings(league)
        return when {
            dataVersion >= standingEntityDb?.dataVersion ?: 0 -> throw ResetContent
            standingEntityDb == null -> throw NoContent
            else -> SoccerStandingMapper.mapToResponseBody(standingEntityDb)
        }
    }

    fun getTeamSchedule(team: String, dataVersion: Long): SoccerTeamSchedule? {
        val teamScheduleEntityDb = soccerTeamScheduleRepository.findTeamSchedule(team)
        return when {
            dataVersion >= teamScheduleEntityDb?.dataVersion ?: 0 -> throw ResetContent
            teamScheduleEntityDb == null -> throw NoContent
            else -> SoccerTeamScheduleMapper.mapToResponseBody(teamScheduleEntityDb)
        }
    }
}