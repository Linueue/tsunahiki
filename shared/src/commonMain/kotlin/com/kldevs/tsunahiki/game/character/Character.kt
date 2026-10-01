package com.kldevs.tsunahiki.game.character

import com.kldevs.tsunahiki.game.LanguageType
import com.kldevs.tsunahiki.game.utils.CanvasStroke

enum class CharacterType {
    Kana,
}

interface ICharacterDescription {
    fun getCharacter(): String
    fun getDisplay(): String
    suspend fun getStrokes(): List<CanvasStroke>
}

data class ProgressionLevel(
    val unlockLevel: Int,
    val name: String,
    val members: List<String>,
    val display: List<String>,
)

interface ICharacterCatalog {
    fun getLanguage() : LanguageType
    fun getAudioPath(filename: String) : String
    fun fromText(text: String): ICharacterDescription
    fun getProgressions(): List<ProgressionLevel>
}