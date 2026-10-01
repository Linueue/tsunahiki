package com.kldevs.tsunahiki.menu

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kldevs.tsunahiki.game.GameEvent
import com.kldevs.tsunahiki.game.GamePhase
import com.kldevs.tsunahiki.game.GameState
import com.kldevs.tsunahiki.game.OnEventFn
import com.kldevs.tsunahiki.game.utils.SpringButton
import com.kldevs.tsunahiki.ui.theme.AppTheme

@Composable
private fun PauseText(text: String, fontSize: TextUnit, color: Color = Color.Unspecified) {
    Text(
        text,
        fontFamily = MaterialTheme.typography.titleMedium.fontFamily,
        fontWeight = FontWeight.W800,
        fontSize = fontSize,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun PauseSlider(title: String, onChange: (Float) -> Unit) {
    var sliderPosition by remember { mutableFloatStateOf(1.0f) }

    val surfaceBright = MaterialTheme.colorScheme.surfaceBright
    val primary = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface

    PauseText(title, 12.sp)
    Slider(
        value = sliderPosition,
        onValueChange = { sliderPosition = it; onChange(it) },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(18.dp),
        colors = SliderDefaults.colors(
            thumbColor = surfaceBright,
            activeTrackColor = primary,
            inactiveTrackColor = surfaceVariant,
            activeTickColor = Color.Transparent,
            inactiveTickColor = Color.Transparent,
        ),
        thumb = {
            // Custom chunky thumb with a border
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(surfaceBright)
                    .border(3.dp, primary, CircleShape),
            )
        },
        track = { sliderState ->
            // Custom track: a chunky rounded bar with tick marks
            val fraction = sliderState.value
            Box(
                modifier = Modifier.fillMaxWidth().height(14.dp).clip(RoundedCornerShape(7.dp))
                    .background(surfaceVariant)
                    .border(2.dp, surfaceVariant, RoundedCornerShape(7.dp)),
            ) {
                // filled portion
                Box(
                    modifier = Modifier.fillMaxWidth(fraction).fillMaxHeight()
                        .clip(RoundedCornerShape(7.dp)).background(primary),
                )
            }
        }
    )
}

@Composable
private fun PauseUI(onEvent: OnEventFn) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(256.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(2.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            PauseText("Pause", fontSize = 23.sp)

            PauseSlider("Music", { v ->
                onEvent(GameEvent.ChangeMusicVolume(v))
            })
            PauseSlider("Sfx", { v ->
                onEvent(GameEvent.ChangeSfxVolume(v))
            })
            SpringButton(
                onClick = {
                    onEvent(GameEvent.Pause(false))
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f),
                containerColor = MaterialTheme.colorScheme.surfaceBright,
                shape = RoundedCornerShape(10.0f),
            ) {
                PauseText("Resume", fontSize = 18.sp)
            }
            SpringButton(
                onClick = {
                    onEvent(GameEvent.Forfeit)
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f),
                containerColor = MaterialTheme.colorScheme.surfaceBright,
                shape = RoundedCornerShape(10.0f),
            ) {
                PauseText("Forfeit", fontSize = 18.sp)
            }
        }
    }
}


@Composable
private fun PauseBackground(gameState: GameState) {
    val bgColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val transparent = Color.Transparent
    val bg by animateColorAsState(
        targetValue = if (gameState.phase == GamePhase.Pause) bgColor else transparent,
        animationSpec = tween(500),
        label = "Bg animation",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
    )
}

@Composable
fun PauseMenu(gameState: GameState, onEvent: OnEventFn) {
    val anim = remember { Animatable(1.0f) }

    LaunchedEffect(gameState.phase) {
        anim.animateTo(
            targetValue = if(gameState.phase == GamePhase.Pause) 0.0f else 1.0f,
            animationSpec = tween(1000),
        )
    }

    PauseBackground(gameState)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                translationY = 1024.0f * 2.0f * anim.value
            },
        contentAlignment = Alignment.Center,
    ) {
        PauseUI(onEvent)
    }
}

@Composable
@Preview
fun PausePreview() {
    AppTheme {
        PauseUI({ e -> Unit })
    }
}