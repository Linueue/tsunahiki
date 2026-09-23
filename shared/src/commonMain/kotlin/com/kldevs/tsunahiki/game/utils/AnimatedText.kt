package com.kldevs.tsunahiki.game.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit

fun lerp(start: Float, end: Float, t: Float): Float = ((end - start) * t + start)

@Composable
fun<T> SpringText(text: String, color: Color = Color.Unspecified, fontFamily: FontFamily?, fontWeight: FontWeight?, fontSize: TextUnit = TextUnit.Unspecified, watchFor: T) {
    val anim = remember { Animatable(1.0f) }

    LaunchedEffect(watchFor) {
        anim.snapTo(1.3f)

        anim.animateTo(
            targetValue = 1.0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow,
            ),
        )
    }

    Box(modifier = Modifier.graphicsLayer {
        scaleX = anim.value
        scaleY = anim.value
    }) {
        Text(
            text,
            color = color,
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            fontSize = fontSize,
        )
    }
}