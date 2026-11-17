package com.ipvio.stream

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SportLeaguesListTest {
    @Test
    fun turkishSwissAndPortugueseLeaguesAreTeamLeagues() {
        for (path in listOf("soccer/tur.1", "soccer/sui.1", "soccer/por.1")) {
            val l = SportData.LEAGUES.firstOrNull { it.path == path }
            assertTrue("missing $path", l != null)
            assertEquals("soccer", l!!.sport)
            assertEquals(SportData.KIND_TEAM, l.kind)
        }
    }

    @Test
    fun leagueNamesAndPathsAreUnique() {
        assertEquals(SportData.LEAGUES.size, SportData.LEAGUES.map { it.name }.toSet().size)
        assertEquals(SportData.LEAGUES.size, SportData.LEAGUES.map { it.path }.toSet().size)
    }
}
