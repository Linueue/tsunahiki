package com.kldevs.tsunahiki.game.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

data class CanvasStroke(
    val points: MutableList<Offset>,
    val color: Color,
)

fun normalizeOffset(size: Offset, position: Offset): Offset
    = Offset(position.x / size.x * 2.0f - 1.0f, position.y / size.y * 2.0f - 1.0f)

fun unnormalizeOffset(size: Offset, position: Offset): Offset
        = Offset((position.x + 1.0f) / 2.0f * size.x, (position.y + 1.0f) / 2.0f * size.y)