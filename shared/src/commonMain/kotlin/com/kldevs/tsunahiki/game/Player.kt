package com.kldevs.tsunahiki.game

data class Levels(
    val kana: Int,
)

data class PlayerDisplay(
    val name: String,
)

data class Player(
    val name: String,
    val levels: Levels,
)