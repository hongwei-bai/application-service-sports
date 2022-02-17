# application-service-sports

CI build state: [![CircleCI](https://circleci.com/gh/hongwei-bai/application-service-sports/tree/main.svg?style=svg)](https://circleci.com/gh/hongwei-bai/application-service-sports/tree/main)

Sports service application are providing apis for NBA schedules, team standings, play-in tournament, playoff etc.

Other than NBA, more leagues are adding to the service.

They are:

- Soccer(top five European leagues): the Premier League, Serie A, Ligue 1, the Bundesliga, and La Liga

- Soccer - UEFA Champions League

- NFL

Android project repository:
[hongwei-bai/android-nba-assist](https://github.com/hongwei-bai/android-nba-assist)

Authentication service(backend) repository
[hongwei-bai/application-service-authentication](https://github.com/hongwei-bai/application-service-authentication)

#### API Samples

###### NBA Standings

``GET`` ``/nba/standing.do?dataVersion=0``

response

```
{
    "dataVersion": 202105181151,
    "western": {
        "teams": [
            {
                "rank": 1,
                "teamAbbr": "UTAH",
                "teamName": "Utah Jazz",
                "wins": 52,
                "losses": 20,
                "pct": 0.722,
                "gamesBack": 0.0,
                "homeRecords": {
                    "first": 31,
                    "second": 5
                },
                "awayRecords": {
                    "first": 21,
                    "second": 15
                },
                "divisionRecords": {
                    "first": 7,
                    "second": 5
                },
                "conferenceRecords": {
                    "first": 28,
                    "second": 14
                },
                "pointsPerGame": 116.4,
                "opponentPointsPerGame": 107.2,
                "avePointsDiff": 9.2,
                "currentStreak": {
                    "first": "W",
                    "second": 2
                },
                "last10Records": {
                    "first": 7,
                    "second": 3
                }
            },
            {
                "rank": 2,
                "teamAbbr": "PHX",
                "teamName": "Phoenix Suns",
                "wins": 51,
                ...
}
```

###### NBA PlayOffs

``GET`` ``/nba/playOff.do?dataVersion=0``

response

```
{
    "dataVersion": 2021052600,
    "seasonOngoing": false,
    "playInOngoing": false,
    "playOffOngoing": true,
    "playIn": {
        "western": {
            "winnerOf78": "lal",
            "loserOf78": "gs",
            "winnerOf910": "mem",
            "loserOf910": "sa",
            "lastWinner": "mem"
        },
        "eastern": {
            "winnerOf78": "bos",
            "loserOf78": "wsh",
            "winnerOf910": "ind",
            "loserOf910": "cha",
            "lastWinner": "wsh"
        }
    },
    "playOff": {
        "western": {
            "round1": {
                "series18": {
                    "scoreHighRank": 4,
                    "scoreLowRank": 1,
                    "winner": "utah"
                },
                "series45": {
                    "scoreHighRank": 4,
                    "scoreLowRank": 3,
                    "winner": "lac"
                },
                "series36": {
                    "scoreHighRank": 4,
                    "scoreLowRank": 2,
                    "winner": "den"
                },
                "series27": {
                    "scoreHighRank": 4,
                    "scoreLowRank": 2,
                    "winner": "phx"
                }
            },
            "round2": {
                "seriesUpper": {
                    "teamFromUpper": "utah",
                    "teamFromLower": "lac",
                    "scoreUpperWinner": 2,
                    "scoreLowerWinner": 2,
                    "winner": "TBD"
                },
                "seriesLower": {
                    "teamFromUpper": "den",
                    "teamFromLower": "phx",
                    "scoreUpperWinner": 0,
                    "scoreLowerWinner": 4,
                    "winner": "phx"
                }
            },
            "conferenceFinal": {
                "teamFromUpper": "TBD",
                "teamFromLower": "phx",
                "scoreUpperWinner": 0,
                "scoreLowerWinner": 0,
                "winner": "TBD"
            }
        },
        "eastern": {
            "round1": {
                "series18": {
                    "scoreHighRank": 4,
                    "scoreLowRank": 1,
                    "winner": "phi"
                },
                "series45": {
                    "scoreHighRank": 1,
                    "scoreLowRank": 4,
                    "winner": "atl"
                },
                "series36": {
                    "scoreHighRank": 4,
                    "scoreLowRank": 0,
                    "winner": "mil"
                },
                "series27": {
                    "scoreHighRank": 4,
                    "scoreLowRank": 1,
                    "winner": "bkn"
                }
            },
            "round2": {
                "seriesUpper": {
                    "teamFromUpper": "phi",
                    "teamFromLower": "atl",
                    "scoreUpperWinner": 2,
                    "scoreLowerWinner": 2,
                    "winner": "TBD"
                },
                "seriesLower": {
                    "teamFromUpper": "mil",
                    "teamFromLower": "bkn",
                    "scoreUpperWinner": 2,
                    "scoreLowerWinner": 2,
                    "winner": "TBD"
                }
            },
            "conferenceFinal": {
                "teamFromUpper": "TBD",
                "teamFromLower": "TBD",
                "scoreUpperWinner": 0,
                "scoreLowerWinner": 0,
                "winner": "TBD"
            }
        },
        "grandFinal": {
            "teamFromWestern": "TBD",
            "teamFromEastern": "TBD",
            "scoreWesternWinner": 0,
            "scoreEasternWinner": 0,
            "winner": "TBD"
        }
    }
}
```

###### Soccer (Serie A) Standings

``GET`` ``/soccer/standing.do?league=ita.1&dataVersion=0``

response

```
{
    "dataVersion": 202107141605,
    "league": "ita.1",
    "leagueTitle": "2020-2021 Italian Serie A",
    "standings": [
        {
            "teamId": 110,
            "teamAbbr": "int",
            "displayName": "Internazionale",
            "shortDisplayName": "Internazionale",
            "logo": "https://a.espncdn.com/i/teamlogos/soccer/500/110.png",
            "wins": 28,
            "losses": 3,
            "draws": 7,
            "gamePlayed": 38,
            "goalsFor": 89,
            "goalsAgainst": 35,
            "points": 91,
            "rankChange": 0,
            "rank": 1,
            "goalDifference": 54,
            "pointDeductions": "",
            "pointsPerGame": 0
        },
        {
            "teamId": 103,
            "teamAbbr": "mil",
            "displayName": "AC Milan",
            "shortDisplayName": "AC Milan",
            "logo": "https://a.espncdn.com/i/teamlogos/soccer/500/103.png",
            "wins": 24,
            "losses": 7,
            "draws": 7,
            "gamePlayed": 38,
            "goalsFor": 74,
            "goalsAgainst": 41,
            "points": 79,
            "rankChange": 0,
            "rank": 2,
            "goalDifference": 33,
            "pointDeductions": "",
            "pointsPerGame": 0
        },
        {
            "teamId": 105,
            "teamAbbr": "ata",
            "displayName": "Atalanta",
            "shortDisplayName": "Atalanta",
            "logo": "https://a.espncdn.com/i/teamlogos/soccer/500/105.png",
            "wins": 23,
            "losses": 6,
            "draws": 9,
            "gamePlayed": 38,
            "goalsFor": 90,
            "goalsAgainst": 47,
            "points": 78,
            "rankChange": 0,
            "rank": 3,
            "goalDifference": 43,
            "pointDeductions": "",
            "pointsPerGame": 0
        },
        {
            "teamId": 111,
            "teamAbbr": "juv",
            "displayName": "Juventus",
            "shortDisplayName": "Juventus",
            "logo": "https://a.espncdn.com/i/teamlogos/soccer/500/111.png",
            "wins": 23,
            "losses": 6,
            "draws": 9,
            "gamePlayed": 38,
            "goalsFor": 77,
            "goalsAgainst": 38,
            "points": 78,
            "rankChange": 0,
            "rank": 4,
            "goalDifference": 39,
            "pointDeductions": "",
            "pointsPerGame": 0
        },
        ....
    ]
}
```
