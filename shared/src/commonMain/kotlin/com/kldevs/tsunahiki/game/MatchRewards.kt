package com.kldevs.tsunahiki.game

import com.kldevs.tsunahiki.GameRules


data class MatchRewards(
    val coinsEarned: Int,
    val xpEarned: Int,
    val newLevel: Int,
    val xpInLevel: Int,
    val winCoins: Int,
) {
    val progress: Float get() = xpInLevel.toFloat() / GameRules.MAX_XP
}

fun calculateRewards(language: LanguageType, isWin: Boolean, score: Int, player: PlayerState): MatchRewards {
    val coinsEarned = score / 15
    val xpEarned = score * 3 / 15

    val lang = player.languages.getLanguage(language)
    val totalXp = lang.xp + xpEarned

    val XP_PER_LEVEL = GameRules.MAX_XP
    val levelsGained = totalXp / XP_PER_LEVEL
    val newLevel = lang.level + levelsGained
    val xpInLevel = totalXp % XP_PER_LEVEL
    val multiplier = if(isWin) 1.5f else 1.0f
    val winCoins = (coinsEarned * multiplier).toInt() - coinsEarned

    return MatchRewards(
        coinsEarned = coinsEarned,
        xpEarned = xpEarned,
        newLevel = newLevel,
        xpInLevel = xpInLevel,
        winCoins = winCoins,
    )
}