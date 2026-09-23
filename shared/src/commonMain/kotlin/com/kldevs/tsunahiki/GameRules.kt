package com.kldevs.tsunahiki

enum class GameGrade {
    Perfect, Good, Okay, Miss,
}

object GameRules {
    val MAX_FLAG_SCORE = 100

    fun grade(cost: Float) = when {
        cost <= 0.15f -> GameGrade.Perfect
        cost <= 0.25f -> GameGrade.Good
        cost <= 0.3f -> GameGrade.Okay
        else -> GameGrade.Miss
    }

    fun scoreDelta(gameGrade: GameGrade) = when(gameGrade) {
        GameGrade.Perfect -> 15
        GameGrade.Good -> 10
        GameGrade.Okay -> 5
        GameGrade.Miss -> 0
    }
    fun flagDelta(gameGrade: GameGrade) = when(gameGrade) {
        GameGrade.Perfect -> -15.0f
        GameGrade.Good -> -10.0f
        GameGrade.Okay -> -5.0f
        GameGrade.Miss -> 0.0f
    }
}