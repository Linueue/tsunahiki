package com.kldevs.tsunahiki.navigation

import com.kldevs.tsunahiki.game.LanguageType
import com.kldevs.tsunahiki.game.character.ICharacterCatalog
import kotlinx.serialization.Serializable

@Serializable
object StartMenuRoute

@Serializable
object LanguageMenuRoute

@Serializable
data class LanguageSettingsMenuRoute(val language: LanguageType)

@Serializable
data class GameplayRoute(val isGuided: Boolean)

@Serializable
object ShopMenuRoute

@Serializable
object ProfileMenuRoute

@Serializable
object GetCoinsMenuRoute