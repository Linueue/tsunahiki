package com.kldevs.tsunahiki.game

import com.russhwolf.settings.Settings

class GameSettings(private val settings: Settings) {
    var musicVolume: Float
        get() = settings.getFloat("music_volume", 1.0f)
        set(value) = settings.putFloat("music_volume", value.coerceIn(0.0f, 1.0f))

    var sfxVolume: Float
        get() = settings.getFloat("sfx_volume", 1.0f)
        set(value) = settings.putFloat("sfx_volume", value.coerceIn(0.0f, 1.0f))
}