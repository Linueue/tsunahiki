package com.kldevs.tsunahiki.audio

interface AudioTag {
    val id: String
}

enum class Sfx(override val id: String) : AudioTag {
    UIClick("ui_click"),
    DrawPerfect("draw_perfect"),
    DrawGood("draw_good"),
    DrawOkay("draw_okay"),
    DrawMiss("draw_miss"),
    DrawCombo("draw_combo")
}

expect class AudioEngine {
    fun loadSfx(sfx: AudioTag, assetPath: String)
    fun playSfx(sfx: AudioTag)

    fun playMusic(assetPath: String, loop: Boolean = true)

    fun stopMusic()

    fun release()
}