package com.kldevs.tsunahiki.game

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import com.kldevs.tsunahiki.GameGrade
import com.kldevs.tsunahiki.GameRules
import com.kldevs.tsunahiki.audio.AudioEngine
import com.kldevs.tsunahiki.audio.AudioString
import com.kldevs.tsunahiki.audio.AudioTag
import com.kldevs.tsunahiki.audio.Sfx
import com.kldevs.tsunahiki.game.character.ICharacterCatalog
import com.kldevs.tsunahiki.game.utils.CanvasStroke
import com.kldevs.tsunahiki.game.utils.similarity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import tsunahiki.shared.generated.resources.Res
import kotlin.math.absoluteValue
import kotlin.time.Duration.Companion.milliseconds

sealed interface GameEvent {
    data class StrokeCommitted(val stroke: CanvasStroke) : GameEvent
    data object Undo : GameEvent
    data object Clear : GameEvent
    data class EnemyPull(val amount: Float) : GameEvent
    data object NextRound : GameEvent
    data object Drawing : GameEvent
    data class Pause(val isPause: Boolean) : GameEvent
    data class ChangeSfxVolume(val volume: Float) : GameEvent
    data class ChangeMusicVolume(val volume: Float) : GameEvent
    data object RequestHearSound : GameEvent
    data class RequestGuide(val forced: Boolean) : GameEvent
    data object GuideFinished : GameEvent
    data object DismissGuideDialog : GameEvent
    data object GuideConfirm : GameEvent
    data object GameOver : GameEvent
    data object GameOverSound : GameEvent
    data object Forfeit : GameEvent
}

typealias OnEventFn = (GameEvent) -> Unit

class GameLogic(
    private val playerRepo: PlayerRepository,
    private val settings: GameSettings,
    private val player: PlayerState,
    private val catalog: ICharacterCatalog,
    private val scope: CoroutineScope,
    private val audioEngine: AudioEngine,
    ) {

    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()
    val enemyAI: EnemyAI = EnemyAI(::onEvent)
    private var aiJob: Job? = null

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
                val flagScore = _state.value.flagScore - event.amount
                _state.update { it.copy(flagScore = flagScore) }
                checkGameOver()
            }
            is GameEvent.NextRound -> {

            }
            is GameEvent.Drawing -> {

            }
            is GameEvent.Pause -> {
                val phase = if(event.isPause) GamePhase.Pause else GamePhase.Drawing
                _state.update { it.copy(phase = phase) }
            }
            is GameEvent.ChangeSfxVolume -> {
                audioEngine.setSfxVolume(event.volume)
                settings.sfxVolume = event.volume
            }
            is GameEvent.ChangeMusicVolume -> {
                audioEngine.setMusicVolume(event.volume)
                settings.musicVolume = event.volume
            }
            is GameEvent.RequestHearSound -> {
                audioEngine.playSfx(AudioString(_state.value.characterDesc!!.getDisplay()))
            }
            is GameEvent.RequestGuide -> {
                if(_state.value.phase == GamePhase.GuideConfirm) {
                    _state.update { it.copy(phase = GamePhase.Drawing, requestGuide = true) }
                    return
                }

                if(_state.value.isGuided)
                    _state.update { it.copy(requestGuide = true) }
                else
                    _state.update { it.copy(phase = GamePhase.GuideDialog) }
            }
            is GameEvent.GuideFinished -> {
                _state.update { it.copy(requestGuide = false) }
            }
            is GameEvent.DismissGuideDialog -> {
                _state.update { it.copy(phase = GamePhase.Drawing) }
            }
            is GameEvent.GuideConfirm -> {
                if(player.coins >= 100) {
                    player.coins -= 100
                    _state.update { it.copy(phase = GamePhase.GuideConfirm) }
                } else {
                    _state.update { it.copy(phase = GamePhase.GuideNotEnoughMoney) }
                }
            }
            is GameEvent.GameOver -> {
                val gameOverState = if(_state.value.flagScore > 0) GameOverState.Lose else GameOverState.Win
                val isWin = gameOverState == GameOverState.Win
                val rewards = calculateRewards(catalog.getLanguage(), isWin, _state.value.score, player)

                val bonus = if(isWin) 1.5f else 1.0f

                player.coins += rewards.coinsEarned + rewards.winCoins
                val lang = player.languages.getLanguage(catalog.getLanguage())
                lang.xp = rewards.xpInLevel
                lang.level = rewards.newLevel


                _state.update { it.copy(phase = GamePhase.GameOver, gameOverState = gameOverState) }
                playerRepo.save(player.toSave())
            }
            is GameEvent.GameOverSound -> {
                audioEngine.stopMusic()
                val gameOverState = if(_state.value.flagScore > 0) GameOverState.Lose else GameOverState.Win

                val sfx = if(gameOverState == GameOverState.Win) Sfx.GameOverWin else Sfx.GameOverLost
                audioEngine.playSfx(sfx)
            }
            is GameEvent.Forfeit -> {
                val gameOverState = GameOverState.Lose
                _state.update { it.copy(score = 0, phase = GamePhase.GameOver, gameOverState = gameOverState) }
                playerRepo.save(player.toSave())

                audioEngine.stopMusic()

                val sfx = if(gameOverState == GameOverState.Win) Sfx.GameOverWin else Sfx.GameOverLost
                audioEngine.playSfx(sfx)
            }
        }
    }

    private fun checkGameOver(): Boolean {
        val isGameOver = _state.value.flagScore.absoluteValue >= GameRules.MAX_FLAG_SCORE

        if(isGameOver) {
            onEvent(GameEvent.GameOver)
        }

        return isGameOver
    }

    private fun requestRandomCharacter(): String {
        val character = catalog.getProgressions().filter {
            it.unlockLevel <= player.languages.getLanguage(catalog.getLanguage()).level
        }.flatMap {
            it.members
        }.random()
        return character
    }

    suspend fun start(isGuided: Boolean = true) {
        audioEngine.playMusic(Res.getUri("files/sfx/music/tense.ogg"))
        audioEngine.loadSfx(Sfx.DrawPerfect, Res.getUri("files/sfx/ui/Rise03.ogg"))
        audioEngine.loadSfx(Sfx.DrawGood, Res.getUri("files/sfx/ui/Rise02.ogg"))
        audioEngine.loadSfx(Sfx.DrawOkay, Res.getUri("files/sfx/ui/Rise01.ogg"))
        audioEngine.loadSfx(Sfx.DrawMiss, Res.getUri("files/sfx/ui/error.mp3"))
        audioEngine.loadSfx(Sfx.GameOverWin, Res.getUri("files/sfx/ui/win.mp3"))
        audioEngine.loadSfx(Sfx.GameOverLost, Res.getUri("files/sfx/ui/error.mp3"))

        // TODO: On multiplayer, it must be synced
        while(_state.value.startingCount >= 1) {
            delay(1000.milliseconds)
            _state.update { it.copy(
                startingCount = it.startingCount - 1,
            ) }
        }

        resolveCharacter(requestRandomCharacter())

        delay(1000.milliseconds)
        _state.update {
            it.copy(
                phase = GamePhase.Drawing,
                isGuided = isGuided,
                requestGuide = isGuided,
                playerDisplay = player.getDisplay(),
                enemyDisplay = enemyAI.getDisplay(),
            )
        }

        aiJob?.cancel()

        aiJob = scope.launch(Dispatchers.Default) {
            enemyAI.loop(state)
        }

        onEvent(GameEvent.RequestHearSound)
        onEvent(GameEvent.RequestGuide(false))
    }

    suspend fun resolveCharacter(nextCharacter: String) {
        val description = catalog.fromText(nextCharacter)
        val strokes = description.getStrokes()
        _state.update { it.copy(
            currentCharacter = nextCharacter,
            characterDesc = description,
            guideStrokes = strokes,
        ) }
        audioEngine.loadSfx(AudioString(description.getDisplay()), Res.getUri(catalog.getAudioPath(description.getDisplay())))
    }

    fun release() {
        audioEngine.release()
    }

    private suspend fun evaluate() {
        _state.update { it.copy(phase = GamePhase.Evaluating) }

        val currentCharacter = _state.value.currentCharacter
        val current = catalog.fromText(currentCharacter!!).getStrokes()
        val cost = withContext(Dispatchers.Default) {
            similarity(strokes, current)
        }
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

        if(checkGameOver())
            return

        // Request for new character from WebSockets
        if(grade != GameGrade.Miss)
            resolveCharacter(requestRandomCharacter())

        delay(500.milliseconds)

        strokes.clear()
        _state.update { it.copy(
            phase = GamePhase.Drawing,
            cost = cost,
        ) }

        onEvent(GameEvent.RequestHearSound)
        onEvent(GameEvent.RequestGuide(false))
    }

}