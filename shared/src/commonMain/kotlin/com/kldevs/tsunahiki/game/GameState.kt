package com.kldevs.tsunahiki.game

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.kldevs.tsunahiki.GameGrade
import com.kldevs.tsunahiki.game.character.ICharacterCatalog
import com.kldevs.tsunahiki.game.character.ICharacterDescription
import com.kldevs.tsunahiki.game.character.KanaDescription
import com.kldevs.tsunahiki.game.character.KanaScript
import com.kldevs.tsunahiki.game.utils.CanvasStroke

enum class GamePhase {
    Starting, Drawing, Evaluating, Resolving, GameOver, Pause, GuideDialog, GuideConfirm, GuideNotEnoughMoney,
}

enum class GameOverState {
    None, Win, Lose,
}

data class GameState(
    var phase: GamePhase = GamePhase.Starting,
    var gameOverState: GameOverState = GameOverState.None,
    var startingCount: Int = 5,
    var score: Int = 0,
    var flagScore: Float = 0.0f,
    var cost: Float = Float.POSITIVE_INFINITY,

    val isGuided: Boolean = true,
    val requestGuide: Boolean = false,
    val showRequestGuideError: Boolean = false,
    var currentCharacter: String? = null,
    var characterDesc: ICharacterDescription? = KanaDescription(KanaScript.Hiragana, "", ""),
    var guideStrokes: List<CanvasStroke> = emptyList(),
    var userStrokes: SnapshotStateList<CanvasStroke> = mutableStateListOf(),
    var recentGrade: GameGrade = GameGrade.Miss,

    val playerDisplay: PlayerDisplay = PlayerDisplay("", 0),
    val enemyDisplay: PlayerDisplay = PlayerDisplay("", 0),
) {
    val totalStrokes: Int get() = userStrokes.size
    val maxStrokes: Int get() = guideStrokes.size
}
