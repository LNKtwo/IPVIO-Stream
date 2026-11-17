package com.ipvio.stream

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SportAlertsLogicTest {
    private val all = SportData.LEAGUES
    private val allOn: (SportData.League) -> Boolean = { true }

    @Test
    fun noFavoritesAsksNoLeague() {
        assertTrue(SportAlertsLogic.favLeagues(all, emptyList(), allOn).isEmpty())
    }

    @Test
    fun soccerFavoriteAsksOnlySoccerTeamLeagues() {
        val ls = SportAlertsLogic.favLeagues(all, listOf("soccer"), allOn)
        assertTrue(ls.isNotEmpty())
        assertTrue(ls.all { it.sport == "soccer" && it.kind == SportData.KIND_TEAM })
        assertTrue(ls.any { it.path == "soccer/tur.1" })
        assertTrue(ls.none { it.path == "football/college-football" })
    }

    @Test
    fun unknownSportFallsBackToAllTeamLeagues() {
        val ls = SportAlertsLogic.favLeagues(all, listOf("soccer", null), allOn)
        assertEquals(all.count { it.kind == SportData.KIND_TEAM }, ls.size)
    }

    @Test
    fun switchedOffLeaguesAreLeftOut() {
        val ls = SportAlertsLogic.favLeagues(all, listOf("soccer"), { it.name != "Süper Lig" })
        assertTrue(ls.none { it.name == "Süper Lig" })
    }

    @Test
    fun delayIsShortWhenLiveOrSoonAndLongOtherwise() {
        val now = 1_000_000L
        assertEquals(SportAlertsLogic.LIVE_MS, SportAlertsLogic.nextDelayMs(listOf(1 to now - 600), now))
        assertEquals(SportAlertsLogic.SOON_MS, SportAlertsLogic.nextDelayMs(listOf(0 to now + 600), now))
        assertEquals(SportAlertsLogic.SOON_MS, SportAlertsLogic.nextDelayMs(listOf(0 to now - 60), now))
        assertEquals(SportAlertsLogic.IDLE_MS, SportAlertsLogic.nextDelayMs(listOf(0 to now + 7200, 2 to now - 9000), now))
        assertEquals(SportAlertsLogic.IDLE_MS, SportAlertsLogic.nextDelayMs(emptyList(), now))
    }
}
