package com.kldevs.tsunahiki.menu

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kldevs.tsunahiki.GameRules
import com.kldevs.tsunahiki.game.GameOverState
import com.kldevs.tsunahiki.game.GamePhase
import com.kldevs.tsunahiki.game.GameState
import com.kldevs.tsunahiki.game.LanguageType
import com.kldevs.tsunahiki.game.MatchRewards
import com.kldevs.tsunahiki.game.PlayerState
import com.kldevs.tsunahiki.game.utils.SpringButton
import com.kldevs.tsunahiki.ui.theme.AppTheme
import org.jetbrains.compose.resources.painterResource
import tsunahiki.shared.generated.resources.Res
import tsunahiki.shared.generated.resources.ui_coins

@Composable
fun MatchRewardsMenu(
    gameState: GameState,
    rewards: MatchRewards,
    onRematch: () -> Unit,
    onSettings: () -> Unit,
) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val surfaceBright = MaterialTheme.colorScheme.surfaceBright
    val surfaceContainerLowest = MaterialTheme.colorScheme.surfaceContainerLowest
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface

    val isGameOver = gameState.phase == GamePhase.GameOver
    val isWin = gameState.gameOverState == GameOverState.Win

    DialogMenu(
        title = "",
        options = listOf(),
        isVisible = isGameOver,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // ---- Header ----
            TextDisplay(
                text = if (isWin) "YOU WIN!" else "YOU LOSE!",
                color = if (isWin) primary else onSurface,
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
            )
            Spacer(Modifier.height(2.dp))
            TextDisplay(
                text = "Match Complete",
                color = onSurface,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )

            Spacer(Modifier.height(20.dp))

            // ---- Score ----
            TextDisplay(
                text = "SCORE",
                color = onSurface,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            TextDisplay(
                text = gameState.score.toString(),
                color = onSurface,
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
            )

            Spacer(Modifier.height(20.dp))

            // ---- Reward card ----
            RewardCard(isGameOver, rewards)

            Spacer(Modifier.height(20.dp))

            // ---- Buttons ----
            SpringButton(
                onClick = onRematch,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                containerColor = primary,
            ) {
                TextDisplay(
                    "REMATCH",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = surfaceBright,
                )
            }
            Spacer(Modifier.height(10.dp))
            SpringButton(
                onClick = onSettings,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.surfaceBright,
            ) {
                TextDisplay(
                    "Go Back",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun RewardCard(isVisible: Boolean, rewards: MatchRewards) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val surfaceBright = MaterialTheme.colorScheme.surfaceBright
    val surfaceContainerLowest = MaterialTheme.colorScheme.surfaceContainerLowest
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface
    val surface = MaterialTheme.colorScheme.surface

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(surface)
            .border(BorderStroke(2.dp, surfaceVariant), RoundedCornerShape(20.dp))
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "COINS COLLECTED",
            color = surfaceContainerLowest,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(Res.drawable.ui_coins),
                contentDescription = "Coins",
                modifier = Modifier.size(26.dp),
            )
            Spacer(Modifier.width(8.dp))

            TextDisplay(
                "+${rewards.coinsEarned}",
                color = onSurface,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
            )

            if(rewards.winCoins != 0) {
                Spacer(Modifier.width(8.dp))

                TextDisplay(
                    "(x1.5) = ${rewards.coinsEarned + rewards.winCoins}",
                    color = onSurface,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W500,
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        DashedDivider()
        Spacer(Modifier.height(14.dp))

        Text(
            "+${rewards.xpEarned} XP",
            color = secondary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
        )
        Spacer(Modifier.height(10.dp))
        XpProgressBar(isVisible, progress = rewards.progress)
        Spacer(Modifier.height(8.dp))
        Text(
            "Level ${rewards.newLevel}",
            color = onSurface,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            "${rewards.xpInLevel} / ${GameRules.MAX_XP} XP to next level",
            color = surfaceContainerLowest,
            fontSize = 10.sp,
        )
    }
}

@Composable
private fun DashedDivider(color: Color? = null) {
    val lineColor = color ?: MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)

    Canvas(
        modifier = Modifier.fillMaxWidth().height(1.dp),
    ) {
        drawLine(
            color = lineColor,
            start = Offset(0f, size.height / 2f),
            end = Offset(size.width, size.height / 2f),
            strokeWidth = size.height,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
        )
    }
}

@Composable
private fun XpProgressBar(isVisible: Boolean, progress: Float) {
    val anim = remember { Animatable(0.0f) }

    LaunchedEffect(isVisible) {
        if(!isVisible)
            return@LaunchedEffect

        anim.snapTo(0.0f)

        anim.animateTo(
            targetValue = progress,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow,
            ),
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(12.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(anim.value.coerceIn(0f, 1f))
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
        )
    }
}

@Composable
@Preview
private fun MatchRewardsPreview() {
    AppTheme {
        val gameState = GameState()
        gameState.phase = GamePhase.GameOver
        gameState.gameOverState = GameOverState.Win

        val rewards = MatchRewards(
            25, 150, 2, 23, 12,
        )

        MatchRewardsMenu(gameState, rewards, {}, {})
    }
}