package com.ipvio.stream

/**
 * Pure rules for the sport alert loop ([SportAlerts.run]), without Android, so JVM tests can cover them.
 * The loop runs for the whole app session; on small sticks every round of downloads and JSON parsing costs
 * noticeable CPU and memory, so it asks only the leagues that can hold a favorite team, and less often when
 * nothing is about to happen.
 */
object SportAlertsLogic {
    const val LIVE_MS = 30_000L
    const val SOON_MS = 60_000L
    const val IDLE_MS = 300_000L

    /** A game that starts within this many seconds (or should have started) counts as "soon". */
    const val SOON_SEC = 900L

    /**
     * Team leagues to ask for favorite teams. [favSports] holds the sport of each favorite team, or null for a
     * favorite whose sport is unknown (saved without a league); then every team sport is asked, as before.
     * Leagues switched off in the sports area ([enabled] false) are left out.
     */
    fun favLeagues(all: List<SportData.League>, favSports: List<String?>, enabled: (SportData.League) -> Boolean): List<SportData.League> {
        if (favSports.isEmpty()) return emptyList()
        val team = all.filter { it.kind == SportData.KIND_TEAM && enabled(it) }
        if (favSports.any { it == null }) return team
        val sports = favSports.filterNotNull().toSet()
        return team.filter { it.sport in sports }
    }

    /**
     * Wait before the next round: short while a game runs or is about to start, long otherwise.
     * [games] are (state, start in epoch seconds) of the watched games; state 0 = scheduled, 1 = live, 2 = over.
     */
    fun nextDelayMs(games: List<Pair<Int, Long>>, nowSec: Long): Long = when {
        games.any { it.first == 1 } -> LIVE_MS
        games.any { it.first == 0 && it.second - nowSec <= SOON_SEC } -> SOON_MS
        else -> IDLE_MS
    }
}
