package com.kldevs.tsunahiki.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kldevs.tsunahiki.GameRules
import com.kldevs.tsunahiki.game.LanguageType
import com.kldevs.tsunahiki.game.PlayerRepository
import com.kldevs.tsunahiki.game.PlayerState
import com.kldevs.tsunahiki.game.utils.SpringButton
import com.kldevs.tsunahiki.navigation.LanguageSettingsMenuRoute
import com.kldevs.tsunahiki.navigation.NavFn
import com.kldevs.tsunahiki.ui.theme.AppTheme
import com.kldevs.tsunahiki.ui.theme.secondaryColor
import org.koin.compose.koinInject
import tsunahiki.shared.generated.resources.Res
import tsunahiki.shared.generated.resources.back
import tsunahiki.shared.generated.resources.profile

@Composable
fun LanguageButton(playerState: PlayerState, languageString: String, onClick: () -> Unit) {
    val surface = MaterialTheme.colorScheme.surface

    SpringButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .padding(10.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp),
        ) {
            val language = playerState.languages.getLanguage(LanguageType.Kana)

            Row(
                modifier = Modifier.fillMaxWidth(0.88f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TextDisplay(languageString, fontSize = 20.sp)

                Box(
                    modifier = Modifier.clip(RoundedCornerShape(15.dp))
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
    }
}

@Composable
private fun LanguageMenuView(playerState: PlayerState, navTo: (Any) -> Unit) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val surfaceBright = MaterialTheme.colorScheme.surfaceBright
    val surfaceContainerLowest = MaterialTheme.colorScheme.surfaceContainerLowest
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface

    TextDisplay(
        "SELECT LANGUAGE",
        fontSize = 23.sp,
    )
    TextDisplay(
        "Pull your way to the top!",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
    )

    HorizontalDivider(
        modifier = Modifier
            .padding(top = 10.dp)
            .clip(RoundedCornerShape(25.dp))
            .fillMaxWidth(0.25f),
        thickness = 5.dp,
        color = primary,
    )

    Spacer(modifier = Modifier.height(12.dp))

    LanguageButton(
        playerState,
        "Japanese (Kana)",
        onClick = {
            navTo(LanguageSettingsMenuRoute(LanguageType.Kana))
        },
    )
    BackgroundColumn(
        modifier = Modifier,
        width = 0.85f,
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
fun LanguageMenu(onBack: NavFn, navTo: (Any) -> Unit) {
    val playerRepo = koinInject<PlayerRepository>()
    val playerState = PlayerState()
    playerState.applySave(playerRepo.load())

    Menu("Languages", onBack = onBack) {
        LanguageMenuView(playerState, navTo)
    }
}

@Composable
@Preview
fun LanguageMenuPreview() {
    AppTheme {
        val playerState = PlayerState()
        playerState.coins = 100
        playerState.languages.getLanguage(LanguageType.Kana).xp = 80
        playerState.languages.getLanguage(LanguageType.Kana).level = 23

        Menu("Languages", onBack = {}) {
            LanguageMenuView(playerState, {})
        }
    }
}