package com.hongwei.service.soccer

import com.google.gson.Gson
import com.hongwei.model.jpa.nba.NbaTeamScheduleRepository
import com.hongwei.model.jpa.soccer.SoccerStandingEntity
import com.hongwei.model.jpa.soccer.SoccerStandingRepository
import com.hongwei.model.jpa.soccer.SoccerTeamDetailRepository
import com.hongwei.model.soccer.espn.SoccerStandingSourceOutput
import com.hongwei.model.soccer.espn.SoccerTeamScheduleSource
import com.hongwei.model.soccer.espn.mapper.SoccerDetailMapper
import com.hongwei.model.soccer.espn.mapper.SoccerStandingMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SoccerAnalysisService {
    @Autowired
    private lateinit var soccerCurlService: EspnSoccerCurlService

    @Autowired
    private lateinit var soccerParseService: EspnSoccerParseService

    @Autowired
    private lateinit var soccerTeamDetailRepository: SoccerTeamDetailRepository

    @Autowired
    private lateinit var soccerStandingRepository: SoccerStandingRepository

    @Autowired
    private lateinit var soccerTeamScheduleRepository: NbaTeamScheduleRepository

    fun fetchTeamFixtures(teamId: Int): String? {
        val jsonString = soccerParseService.parseTeamFixtures(soccerCurlService.getTeamFixtures(teamId))
        val sourceObject = Gson().fromJson(jsonString, SoccerTeamScheduleSource::class.java)
        return Gson().toJson(sourceObject)
    }

    fun fetchTeamResults(teamId: Int): String? {
        val jsonString = soccerParseService.parseTeamResults(soccerCurlService.getTeamResults(teamId))
        val sourceObject = Gson().fromJson(jsonString, SoccerTeamScheduleSource::class.java)
        return Gson().toJson(sourceObject)
    }

    fun fetchStandings(league: String): SoccerStandingEntity? {
        val jsonString = soccerParseService.parseStandingInfo(soccerCurlService.getStanding())
        val sourceObject = Gson().fromJson(jsonString, SoccerStandingSourceOutput::class.java)

        SoccerDetailMapper.map(league, sourceObject)?.forEach {
            val teamDetailDb = soccerTeamDetailRepository.findTeamDetailById(it.id)
            if (teamDetailDb == null) {
                soccerTeamDetailRepository.save(it)
            }
        }
        val standingEntityDb = soccerStandingRepository.findStandings(league)?.firstOrNull()
        val standingEntity = SoccerStandingMapper.map(league, sourceObject)
        if (standingEntityDb == standingEntity) {
            return standingEntityDb
        }
        soccerStandingRepository.save(standingEntity)
        return standingEntity
    }
}