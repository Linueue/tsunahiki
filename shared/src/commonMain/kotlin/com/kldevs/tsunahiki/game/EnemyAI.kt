package com.kldevs.tsunahiki.game

import androidx.compose.runtime.State
import com.kldevs.tsunahiki.GameRules
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

class EnemyAI(
    val onEventFn: OnEventFn
) {
    fun getDisplay(): PlayerDisplay {
        val name = listOf(
            "Kiki", "Aki", "Kari", "Nari", "Bob",
        ).random()
        val avatar = (0 until GameAvatars.entries.size).random()
        return PlayerDisplay(name, avatar)
    }

    suspend fun loop(gameState: StateFlow<GameState>) {
        while(gameState.value.phase != GamePhase.GameOver) {
            if(gameState.value.phase == GamePhase.Pause) {
                delay(1000.milliseconds)
                continue
            }

            val delayMs = (500..5000).random()

            delay(delayMs.milliseconds)

            val start = 0.1f
            val end = 1.5f
            val pullCost = start + Random.nextFloat() * (end - start)

            val grade = GameRules.grade(pullCost)
            val flag = GameRules.flagDelta(grade)

            onEventFn(GameEvent.EnemyPull(flag))
        }
    }
}