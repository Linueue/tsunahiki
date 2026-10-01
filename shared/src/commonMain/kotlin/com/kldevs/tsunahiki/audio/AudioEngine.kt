package com.kldevs.tsunahiki.audio

interface AudioTag {
    val id: String
}

data class AudioString(override val id: String) : AudioTag

enum class Sfx(override val id: String) : AudioTag {
    UIClick("ui_click"),
    DrawPerfect("draw_perfect"),
    DrawGood("draw_good"),
    DrawOkay("draw_okay"),
    DrawMiss("draw_miss"),
    DrawCombo("draw_combo"),
    GameOverWin("gameover_win"),
    GameOverLost("gameover_lost"),
}

expect class AudioEngine {
    fun setSfxVolume(volume: Float)
    fun setMusicVolume(volume: Float)
    fun loadSfx(sfx: AudioTag, assetPath: String)
    fun playSfx(sfx: AudioTag)

    fun playMusic(assetPath: String, loop: Boolean = true)

    fun stopMusic()

    fun release()
}