package com.hongwei.service.soccer

import com.google.gson.Gson
import com.hongwei.model.jpa.soccer.*
import com.hongwei.model.soccer.bbc.BbcSoccerChampionLeagueSource
import com.hongwei.model.soccer.espn.SoccerStandingSourceOutput
import com.hongwei.model.soccer.espn.SoccerTeamEventSource
import com.hongwei.model.soccer.espn.SoccerTeamScheduleSource
import com.hongwei.model.soccer.espn.mapper.SoccerDetailMapper
import com.hongwei.model.soccer.espn.mapper.SoccerStandingMapper
import com.hongwei.model.soccer.espn.mapper.SoccerTeamScheduleMapper
import com.hongwei.util.DateTimeUtil
import com.hongwei.util.TeamColorUtil.convertColorHexStringToLong
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SoccerAnalysisService {
    private val logger: Logger = LogManager.getLogger(SoccerAnalysisService::class.java)

    @Autowired
    private lateinit var soccerCurlService: EspnSoccerCurlService

    @Autowired
    private lateinit var soccerParseService: EspnSoccerParseService

    @Autowired
    private lateinit var soccerTeamDetailRepository: SoccerTeamDetailRepository

    @Autowired
    private lateinit var soccerStandingRepository: SoccerStandingRepository

    @Autowired
    private lateinit var soccerTeamScheduleRepository: SoccerTeamScheduleRepository

    @Autowired
    private lateinit var bbcSoccerCurlService: BbcSoccerCurlService

    @Autowired
    private lateinit var bbcSoccerParseService: BbcSoccerParseService

    fun fetchChampionLeagueSchedules(): BbcSoccerChampionLeagueSource? {
        val currentYearMonth = DateTimeUtil.getCurrentYearMonth()
        val doc = bbcSoccerCurlService.getSchedule(currentYearMonth.first, currentYearMonth.second)
        val jsonString = bbcSoccerParseService.parseMonthSchedule(doc)
        return Gson().fromJson(jsonString, BbcSoccerChampionLeagueSource::class.java)
    }

    fun fetchTeamSchedules(teamId: Long): SoccerTeamScheduleEntity? {
        addQueryTeam(teamId)
        val teamFixturesSource = fetchTeamFixtures(teamId)
        logger.debug(teamFixturesSource)
        val entityDb = soccerTeamScheduleRepository.findTeamSchedule(teamId)
        val teamDetail = soccerTeamDetailRepository.findTeamDetailById(teamId)
        if (teamDetail != null) {
            val fixturesEntity = SoccerTeamScheduleMapper.map(teamDetail, teamFixturesSource)
            if (entityDb?.events != fixturesEntity?.events) {
                val finishedEventsSource = mutableListOf<SoccerTeamEventSource>()
                teamFixturesSource?.run {
                    updateTeamColor(teamId, teamFixturesSource)
                    getRelatedLeagues(teamId, teamFixturesSource).forEach { league ->
                        fetchTeamResults(teamId, league)?.events?.let { finishedEventsSource.addAll(it) }
                    }
                }
                val fullEntity = SoccerTeamScheduleMapper.map(teamDetail, teamFixturesSource, finishedEventsSource)
                if (fullEntity != null && fullEntity.finishedEvents.isNotEmpty() && entityDb?.finishedEvents != fullEntity.finishedEvents) {
                    soccerTeamScheduleRepository.save(fullEntity)
                    return fullEntity
                }
            }
        }
        return entityDb
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
        val standingEntityDb = soccerStandingRepository.findStandings(league)
        val standingEntity = SoccerStandingMapper.map(league, sourceObject)
        if (standingEntityDb == standingEntity) {
            return standingEntityDb
        }
        soccerStandingRepository.save(standingEntity)
        return standingEntity
    }

    fun initializeLeagues(leagues: List<String> = DEFAULT_LEAGUES) {
        leagues.forEach { league ->
            soccerStandingRepository.findStandings(league) ?: fetchStandings(league)
        }
    }

    fun getQueryingTeams(): List<SoccerTeamDetailEntity> = soccerTeamDetailRepository.findQueryingTeams()
            ?: emptyList()

    fun getQueryingLeagues(): List<String> = soccerTeamDetailRepository.findQueryingTeams()?.map { it.league }?.distinct()
            ?: emptyList()

    private fun addQueryTeam(teamId: Long) {
        val teamDetailDb = soccerTeamDetailRepository.findTeamDetailById(teamId)
        teamDetailDb?.run {
            if (isQuerying) {
                return
            }

            teamDetailDb.isQuerying = true
            soccerTeamDetailRepository.save(teamDetailDb)
        }
    }

    private fun fetchTeamFixtures(teamId: Long): SoccerTeamScheduleSource? {
        val jsonString = soccerParseService.parseTeamFixtures(soccerCurlService.getTeamFixtures(teamId))
        return Gson().fromJson(jsonString, SoccerTeamScheduleSource::class.java)
    }

    private fun fetchTeamResults(teamId: Long, league: String): SoccerTeamScheduleSource? {
        val jsonString = soccerParseService.parseTeamResults(soccerCurlService.getTeamResults(teamId, league))
        return Gson().fromJson(jsonString, SoccerTeamScheduleSource::class.java)
    }

    private fun updateTeamColor(teamId: Long, soccerTeamScheduleSource: SoccerTeamScheduleSource) {
        val teamDetailDb = soccerTeamDetailRepository.findTeamDetailById(teamId)
        if (soccerTeamScheduleSource.team.id == teamDetailDb?.id && soccerTeamScheduleSource.team.teamColor.isNotEmpty()) {
            teamDetailDb.teamColor = convertColorHexStringToLong(soccerTeamScheduleSource.team.teamColor)
            soccerTeamDetailRepository.save(teamDetailDb)
        }
    }

    private fun getRelatedLeagues(teamId: Long, soccerTeamScheduleSource: SoccerTeamScheduleSource): List<String> {
        soccerTeamDetailRepository.findTeamDetailById(teamId)?.league?.let { mainLeague ->
            val nationPrefix = mainLeague.split(".").firstOrNull()?.toUpperCase()
            return soccerTeamScheduleSource.dropdownLeagues.filter {
                it.value.split(".").firstOrNull()?.toUpperCase() == nationPrefix
                        || EUROPE_COMMON_LEAGUES.contains(it.value.toUpperCase())
            }.map { it.value.toUpperCase() }
        }
        return emptyList()
    }

    companion object {
        val EUROPE_COMMON_LEAGUES = listOf(
                "UEFA.CHAMPIONS",
                "UEFA.EUROPA",
                "UEFA.EUROPA_QUAL",
                "CLUB.FRIENDLY"
        )

        val DEFAULT_LEAGUES = listOf(
                "ita.1", "ger.1", "eng.1", "esp.1", "fra.1"
        )
    }
}