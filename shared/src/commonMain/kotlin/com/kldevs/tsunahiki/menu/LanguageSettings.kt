package com.kldevs.tsunahiki.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.kldevs.tsunahiki.game.character.ICharacterCatalog
import com.kldevs.tsunahiki.game.character.KanaCatalog
import com.kldevs.tsunahiki.game.utils.SpringButton
import com.kldevs.tsunahiki.navigation.GameplayRoute
import com.kldevs.tsunahiki.navigation.NavFn
import com.kldevs.tsunahiki.ui.theme.AppTheme
import com.kldevs.tsunahiki.ui.theme.secondaryColor
import dev.piotrprus.particleemitter.CanvasEmitterConfig
import dev.piotrprus.particleemitter.CanvasParticleEmitter
import dev.piotrprus.particleemitter.ParticleShape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import tsunahiki.shared.generated.resources.Res
import tsunahiki.shared.generated.resources.back
import tsunahiki.shared.generated.resources.play
import tsunahiki.shared.generated.resources.profile
import tsunahiki.shared.generated.resources.ui_lock

@Composable
private fun LevelCard(
    level: Int,
    title: String,
    kana: List<String>,
    unlocked: Boolean,
    onClick: () -> Unit,
) {
    val secondary = MaterialTheme.colorScheme.secondary
    val surfaceBright = MaterialTheme.colorScheme.surfaceBright
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface

    BackgroundColumn(
        modifier = Modifier
            .then(if (unlocked) Modifier.clickable(onClick = onClick) else Modifier),
        width = 1.0f,
        isBorderDashed = !unlocked,
        borderColor = if (unlocked) secondary else surfaceVariant,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TextDisplay(
                title,
                fontSize = 16.sp,
                fontWeight = FontWeight.W600,
                color = onSurface,
            )

            if (unlocked) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(secondary.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 3.dp),
                ) {
                    TextDisplay(
                        "Unlocked",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.W600,
                        color = secondary,
                    )
                }
            } else {
                Image(
                    painter = painterResource(Res.drawable.ui_lock),
                    contentDescription = "Locked",
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        Spacer(Modifier.height(2.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            kana.forEach { k ->
                val charColor = if (unlocked) onSurface else onSurface.copy(alpha = 0.35f)
                val charBg = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                val charBorder = surfaceVariant

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(charBg)
                        .border(1.dp, charBorder, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    TextDisplay(
                        k,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W500,
                        color = charColor,
                    )
                }
            }
        }
    }
}

@Composable
private fun ToggleRow(
    options: List<Pair<String, String?>>,   // label to optional "Coming Soon" subtitle
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth(0.88f)
            .clip(RoundedCornerShape(30.dp))
            .background(surfaceVariant.copy(alpha = 0.5f)),
    ) {
        options.forEachIndexed { i, (label, subtitle) ->
            val selected = i == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(if (selected) (if (i == 0) secondary else primary) else Color.Transparent)
                    .clickable(enabled = subtitle == null) { onSelect(i) },
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    TextDisplay(
                        label,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W700,
                        color = if (selected) Color.White else onSurface.copy(alpha = 0.6f),
                    )
                    if (subtitle != null) {
                        TextDisplay(
                            subtitle,
                            fontSize = 11.sp,
                            color = onSurface.copy(alpha = 0.35f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageSettingsView(language: LanguageType, playerState: PlayerState, navTo: (Any) -> Unit) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface

    var opponent by remember { mutableStateOf(0) } // 0 = AI, 1 = Human
    var mode by remember { mutableStateOf(0) }     // 0 = Guided, 1 = Unguided

    var catalog by remember { mutableStateOf<ICharacterCatalog?>(null) }

    // TODO: Make this generic
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            catalog = KanaCatalog.load()
        }
    }

    if(catalog == null) {
        LoadingView()
        return
    }

    val levels = catalog!!.getProgressions().map {
        Triple(
            it.unlockLevel.toString() + " - " + it.name,
            it.members,
            it.unlockLevel <= playerState.languages.getLanguage(language).level,
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(10.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1.0f)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            levels.forEachIndexed { i, (title, kana, unlocked) ->
                LevelCard(
                    level = i + 1,
                    title = title,
                    kana = kana,
                    unlocked = unlocked,
                    onClick = {},
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .weight(1.0f)
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceBright)
                .border(2.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            TextDisplay(
                "MATCH SETTINGS",
                fontSize = 20.sp,
                fontWeight = FontWeight.W800,
                color = onSurface,
            )

            HorizontalDivider(
                modifier = Modifier.padding(top = 8.dp).clip(CircleShape).width(90.dp),
                thickness = 4.dp,
                color = primary,
            )

            Spacer(Modifier.height(20.dp))

            TextDisplay(
                "Opponent",
                fontSize = 13.sp,
                fontWeight = FontWeight.W500,
                color = onSurface.copy(alpha = 0.7f),
            )
            ToggleRow(
                options = listOf(
                    "VS AI" to null,
                    "VS Human" to "Coming Soon",
                ),
                selectedIndex = opponent,
                onSelect = { opponent = it },
            )

            Spacer(Modifier.height(16.dp))

            TextDisplay(
                "Mode",
                fontSize = 13.sp,
                fontWeight = FontWeight.W500,
                color = onSurface.copy(alpha = 0.7f),
            )
            ToggleRow(
                options = listOf(
                    "Guided" to null,
                    "Unguided" to null,
                ),
                selectedIndex = mode,
                onSelect = { mode = it },
            )

            Spacer(Modifier.height(24.dp))

            SpringButton(
                onClick = {
                    navTo(GameplayRoute(isGuided = (mode == 0)))
                },
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier.fillMaxWidth(0.88f).height(60.dp),
                containerColor = primary,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    TextDisplay(
                        "START MATCH",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W800,
                        color = Color.White,
                    )
                    Image(
                        painter = painterResource(Res.drawable.play),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun LanguageSettings(language: LanguageType, onBack: NavFn, navTo: (Any) -> Unit) {
    val playerRepo = koinInject<PlayerRepository>()
    val playerState = PlayerState()
    playerState.applySave(playerRepo.load())

    val languageData = playerState.languages.getLanguage(language)

    // For now
    Menu("Japanese", onBack = onBack, topContent = {
        Box(
            modifier = Modifier.clip(RoundedCornerShape(15.dp))
                .background(secondaryColor.copy(alpha = 0.5f))
                .padding(start = 10.dp, end = 10.dp)
        ) {
            TextDisplay(
                "Lvl ${languageData.level}",
                fontSize = 12.sp,
            )
        }
    }) {
        LanguageSettingsView(language, playerState, navTo)
    }
}

@Composable
@Preview
fun LanguageSettingsPreview() {
    AppTheme {
        val playerState = PlayerState()
        playerState.coins = 100
        playerState.languages.getLanguage(LanguageType.Kana).xp = 80
        playerState.languages.getLanguage(LanguageType.Kana).level = 23

        // For now
        Menu("Japanese", onBack = {}, topContent = {
            Box(
                modifier = Modifier.clip(RoundedCornerShape(15.dp))
                    .background(secondaryColor.copy(alpha = 0.5f))
                    .padding(start = 10.dp, end = 10.dp)
            ) {
                TextDisplay(
                    "Lvl 15",
                    fontSize = 12.sp,
                )
            }
        }) {
            LanguageSettingsView(LanguageType.Kana, playerState, {})
        }
    }
}