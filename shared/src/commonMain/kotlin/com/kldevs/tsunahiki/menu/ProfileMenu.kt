package com.kldevs.tsunahiki.menu

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kldevs.tsunahiki.GameRules
import com.kldevs.tsunahiki.game.GameAvatars
import com.kldevs.tsunahiki.game.LanguageType
import com.kldevs.tsunahiki.game.PlayerRepository
import com.kldevs.tsunahiki.game.PlayerState
import com.kldevs.tsunahiki.navigation.NavFn
import com.kldevs.tsunahiki.ui.theme.AppTheme
import com.kldevs.tsunahiki.ui.theme.secondaryColor
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import tsunahiki.shared.generated.resources.Res
import tsunahiki.shared.generated.resources.profile
import tsunahiki.shared.generated.resources.ui_coins

@Composable
fun LevelSlider(value: Float) {
    val surfaceBright = MaterialTheme.colorScheme.surfaceBright
    val primary = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val anim = remember { Animatable(0.0f) }

    LaunchedEffect(Unit) {
        anim.snapTo(0.0f)

        anim.animateTo(
            targetValue = value,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow,
            ),
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(12.dp).clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Box(
                modifier = Modifier.fillMaxHeight().fillMaxWidth(anim.value.coerceIn(0f, 1f))
                    .clip(CircleShape).background(MaterialTheme.colorScheme.primary),
            )
        }
    }
}

@Composable
private fun PlayerMenuView(playerState: PlayerState) {
    val surfaceBright = MaterialTheme.colorScheme.surfaceBright
    val surfaceContainerLowest = MaterialTheme.colorScheme.surfaceContainerLowest
    val primary = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .padding(10.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(primary)
                .border(2.dp, onSurface, RoundedCornerShape(12.dp))
                .padding(10.dp),
        ) {
            Image(
                painter = painterResource(GameAvatars.getDrawable(playerState.avatar)),
                contentDescription = "Profile",
                contentScale = ContentScale.FillBounds,
            )
        }
        TextDisplay(playerState.name)
    }

    Spacer(modifier = Modifier.height(2.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .padding(10.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(surfaceBright)
            .border(2.dp, surfaceVariant, RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Image(
            painter = painterResource(Res.drawable.ui_coins),
            contentDescription = "Coins",
            modifier = Modifier.size(18.dp),
        )
        TextDisplay(
            playerState.coins.toString(),
            fontSize = 18.sp,
        )
    }

    Spacer(modifier = Modifier.height(12.dp))
    TextDisplay("LANGUAGE PROGRESS", fontSize = 23.sp)

    HorizontalDivider(
        modifier = Modifier
            .padding(top = 18.dp, bottom = 18.dp)
            .clip(RoundedCornerShape(25.dp))
            .fillMaxWidth(0.25f),
        thickness = 5.dp,
        color = primary,
    )

    BackgroundColumn(
        modifier = Modifier,
    ) {
        val language = playerState.languages.getLanguage(LanguageType.Kana)

        Row(
            modifier = Modifier
                .fillMaxWidth(0.88f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TextDisplay("Japanese", fontSize = 20.sp)

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(15.dp))
                    .background(secondaryColor.copy(alpha = 0.5f))
                    .padding(start = 10.dp, end = 10.dp)
            ) {
                TextDisplay(
                    "Lvl ${language.level}",
                    fontSize = 12.sp,
                )
            }
        }
        LevelSlider(language.xp.toFloat() / GameRules.MAX_LEVELS.getLanguage(LanguageType.Kana).xp.toFloat())

        val xp = language.xp
        val maxXP = GameRules.MAX_LEVELS.getLanguage(LanguageType.Kana).xp

        TextDisplay(
            "${xp}/${maxXP} XP",
            fontSize = 15.sp,
            fontWeight = FontWeight.W500,
        )
    }
    BackgroundColumn(
        modifier = Modifier,
        isBorderDashed = true,
        borderColor = surfaceVariant,
    ) {
        TextDisplay(
            "More languages coming soon",
            fontSize = 15.sp,
            fontWeight = FontWeight.W500,
            color = surfaceContainerLowest,
        )
    }
}

@Composable
fun ProfileMenu(onBack: NavFn) {
    val player = koinInject<PlayerRepository>()
    val playerState = PlayerState()
    playerState.applySave(player.load())

    Menu("Profile", onBack = onBack) {
        PlayerMenuView(playerState)
    }
}

@Composable
@Preview
fun ProfileMenuPreview() {
    AppTheme {
        val playerState = PlayerState()
        playerState.coins = 100
        playerState.languages.getLanguage(LanguageType.Kana).xp = 80
        playerState.languages.getLanguage(LanguageType.Kana).level = 23

        Menu("Profile", onBack = {}) {
            PlayerMenuView(playerState)
        }
    }
}