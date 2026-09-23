package com.kldevs.tsunahiki.game

import androidx.compose.runtime.mutableStateListOf
import com.kldevs.tsunahiki.GameGrade
import com.kldevs.tsunahiki.GameRules
import com.kldevs.tsunahiki.audio.AudioEngine
import com.kldevs.tsunahiki.audio.Sfx
import com.kldevs.tsunahiki.game.character.ICharacterCatalog
import com.kldevs.tsunahiki.game.utils.CanvasStroke
import com.kldevs.tsunahiki.game.utils.similarity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import tsunahiki.shared.generated.resources.Res
import kotlin.time.Duration.Companion.milliseconds

sealed interface GameEvent {
    data class StrokeCommitted(val stroke: CanvasStroke) : GameEvent
    data object Undo : GameEvent
    data object Clear : GameEvent
    data class EnemyPull(val amount: Float) : GameEvent
    data object NextRound : GameEvent
    data object Drawing : GameEvent
}

typealias OnEventFn = (GameEvent) -> Unit

class GameLogic(
    private val catalog: ICharacterCatalog,
    private val scope: CoroutineScope,
    private val audioEngine: AudioEngine,
    ) {

    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    private val strokes = _state.value.userStrokes

    fun onEvent(event: GameEvent) {
        when(event) {
            is GameEvent.StrokeCommitted -> {
                if(_state.value.phase != GamePhase.Drawing)
                    return

                strokes.add(event.stroke)

                if(strokes.size == _state.value.maxStrokes)
                    scope.launch { evaluate() }
            }
            is GameEvent.Undo -> {
                strokes.removeLastOrNull()
            }
            is GameEvent.Clear -> {
                strokes.clear()
            }
            is GameEvent.EnemyPull -> {
                val flagScore = _state.value.flagScore + event.amount
            }
            is GameEvent.NextRound -> {

            }
            is GameEvent.Drawing -> {

            }
        }
    }

    suspend fun start() {
        resolveCharacter("ざ")
        _state.update { it.copy(phase = GamePhase.Drawing) }

        audioEngine.playMusic(Res.getUri("files/sfx/music/tense.ogg"))
        audioEngine.loadSfx(Sfx.DrawPerfect, Res.getUri("files/sfx/ui/Rise03.ogg"))
        audioEngine.loadSfx(Sfx.DrawGood, Res.getUri("files/sfx/ui/Rise02.ogg"))
        audioEngine.loadSfx(Sfx.DrawOkay, Res.getUri("files/sfx/ui/Rise01.ogg"))
        audioEngine.loadSfx(Sfx.DrawMiss, Res.getUri("files/sfx/ui/error.mp3"))
    }

    suspend fun resolveCharacter(nextCharacter: String) {
        val description = catalog.fromText(nextCharacter)
        val strokes = description.getStrokes()
        _state.update { it.copy(
            currentCharacter = nextCharacter,
            characterDesc = description,
            guideStrokes = strokes,
        ) }
    }

    fun release() {
        audioEngine.release()
    }

    private suspend fun evaluate() {
        _state.update { it.copy(phase = GamePhase.Evaluating) }

        val currentCharacter = _state.value.currentCharacter
        val current = catalog.fromText(currentCharacter!!).getStrokes()
        val cost = similarity(strokes, current)
        val grade = GameRules.grade(cost)
        val scoreDelta = GameRules.scoreDelta(grade)
        val flagDelta = GameRules.flagDelta(grade)

        _state.update { it.copy(
            phase = GamePhase.Resolving,
            score = it.score + scoreDelta,
            flagScore = it.flagScore + flagDelta,

            recentGrade = grade,
        ) }

        when(grade) {
            GameGrade.Perfect -> audioEngine.playSfx(Sfx.DrawPerfect)
            GameGrade.Good -> audioEngine.playSfx(Sfx.DrawGood)
            GameGrade.Okay -> audioEngine.playSfx(Sfx.DrawOkay)
            GameGrade.Miss -> audioEngine.playSfx(Sfx.DrawMiss)
        }

        // Request for new character from WebSockets

        delay(500.milliseconds)

        strokes.clear()
        _state.update { it.copy(
            phase = GamePhase.Drawing,
            cost = cost,
        ) }
    }

}