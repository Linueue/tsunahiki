package com.kldevs.tsunahiki.game.character

import com.kldevs.tsunahiki.game.utils.CanvasStroke

enum class CharacterType {
    Kana,
}

interface ICharacterDescription {
    fun getCharacter(): String
    fun getDisplay(): String
    suspend fun getStrokes(): List<CanvasStroke>
}

interface ICharacterCatalog {
    fun fromText(text: String): ICharacterDescription
}