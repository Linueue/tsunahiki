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
import com.kldevs.tsunahiki.game.utils.CanvasStroke

enum class GamePhase {
    Starting, Drawing, Evaluating, Resolving, GameOver,
}

data class GameState(
    var phase: GamePhase = GamePhase.Starting,
    var score: Int = 0,
    var flagScore: Float = 0.0f,
    var cost: Float = Float.POSITIVE_INFINITY,

    var currentCharacter: String? = null,
    var characterDesc: ICharacterDescription? = null,
    var guideStrokes: List<CanvasStroke> = emptyList(),
    var userStrokes: SnapshotStateList<CanvasStroke> = mutableStateListOf(),
    var recentGrade: GameGrade = GameGrade.Miss,

    val playerName: String = "Aki",
    val enemyName: String = "Enemy"
) {
    val totalStrokes: Int get() = userStrokes.size
    val maxStrokes: Int get() = guideStrokes.size
}
