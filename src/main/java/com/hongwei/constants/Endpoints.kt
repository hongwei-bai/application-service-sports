package com.hongwei.constants

object Endpoints {
    object Espn {
        const val STANDING = "https://www.espn.com/nba/standings"

        const val TRANSACTIONS = "https://www.espn.com/nba/transactions"
        
        object Soccer {
            const val SERIEA_STANDING = "https://www.espn.com.au/football/standings/_/league/{league}"

            const val SERIEA_TEAM_SCHEDILE = "https://www.espn.com.au/{path}"
        }
    }
}