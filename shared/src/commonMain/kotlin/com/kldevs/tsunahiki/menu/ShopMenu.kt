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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kldevs.tsunahiki.game.GameAvatar
import com.kldevs.tsunahiki.game.GameAvatars
import com.kldevs.tsunahiki.game.PlayerRepository
import com.kldevs.tsunahiki.game.PlayerState
import com.kldevs.tsunahiki.game.utils.SpringButton
import com.kldevs.tsunahiki.navigation.NavFn
import com.kldevs.tsunahiki.ui.theme.AppTheme
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import tsunahiki.shared.generated.resources.Res
import tsunahiki.shared.generated.resources.cart
import tsunahiki.shared.generated.resources.ui_coins

private sealed interface ShopPhase {
    data object Idle : ShopPhase
    data class ConfirmingAvatar(val avatarId: Int) : ShopPhase

    data object NotEnoughCoins : ShopPhase
    data object Confirmed : ShopPhase
}

@Composable
private fun AvatarView(playerState: PlayerState, idx: Int, avatar: GameAvatar, onSelect: (Int) -> Unit, onAvatarClicked: (Int) -> Unit) {
    val isSelected = playerState.avatar == idx
    val isUnlocked = playerState.isUnlockedAvatar(idx)
    val borderColor = if(isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant
    val borderWidth = if(isSelected) 5.dp else 2.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceBright)
            .border(borderWidth, borderColor, RoundedCornerShape(12.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Image(
            painter = painterResource(avatar.drawable),
            contentDescription = avatar.name,
        )
        TextDisplay(
            avatar.name,
            fontSize = 12.sp,
        )
        
        val action = when {
            isSelected -> null
            isUnlocked -> ({ onSelect(idx) })
            else -> ({ onAvatarClicked(idx) })
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp)
                .padding(10.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .then(if(action != null) Modifier.clickable(onClick = action) else Modifier),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            if(isSelected) {
                TextDisplay("Equipped", fontSize = 15.sp)
                return@Row
            }
            if(isUnlocked) {
                TextDisplay("Unlocked", fontSize = 15.sp)
                return@Row
            }

            Image(
                painter = painterResource(Res.drawable.ui_coins),
                contentDescription = "Coins",
                modifier = Modifier
                    .size(18.dp),
            )
            TextDisplay(
                avatar.price.toString(),
                fontSize = 15.sp,
            )
        }
    }
}

@Composable
fun ShopMenuView(playerState: PlayerState, onAvatarClicked: (Int) -> Unit) {
    var selected by remember { mutableStateOf("Avatar") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            val texts = listOf(
                "Avatar",
                "Borders",
            )

            texts.forEach { text ->
                val color = if(text == selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceBright
                val textColor = if(text == selected) MaterialTheme.colorScheme.surfaceBright else MaterialTheme.colorScheme.surfaceContainerLow

                Box(
                    modifier = Modifier
                        .weight(1.0f)
                        .padding(5.dp),
                ) {
                    SpringButton(
                        onClick = {
                            selected = text
                        },
                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        containerColor = color,
                    ) {
                        TextDisplay(
                            text,
                            fontSize = 18.sp,
                            color = textColor,
                        )
                    }
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            modifier = Modifier
                .fillMaxSize(),
        ) {
            if(selected == "Avatar") {
                items(GameAvatars.entries.size) { idx ->
                    AvatarView(playerState, idx, GameAvatars.entries[idx], onSelect = {
                        playerState.avatar = idx
                    }, onAvatarClicked)
                }
            } else {
                items(GameAvatars.entries.size) { idx ->
                    AvatarView(playerState, idx, GameAvatars.entries[idx],onSelect = {
                        playerState.avatar = idx
                    }, onAvatarClicked)
                }
            }
        }
    }
}

@Composable
private fun ConfirmAvatarView(phase: ShopPhase, onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    var isVisible by remember { mutableStateOf(false) }
    var avatarId by remember { mutableStateOf(0) }
    val avatar = GameAvatars.entries[avatarId]

    LaunchedEffect(phase) {
        when(phase) {
            is ShopPhase.ConfirmingAvatar -> {
                isVisible = true
                avatarId = phase.avatarId
            }
            else -> { isVisible = false }
        }
    }

    DialogMenu(
        title = "Confirm Purchase",
        isVisible = isVisible,
        options = listOf(
            DialogOption("No", onDismiss, true),
            DialogOption("Yes", { onConfirm(avatarId) }, false),
        ),
    ) {
        TextDisplay(
            "Are you sure you want to buy ${avatar.name}?",
            fontSize = 15.sp,
            fontWeight = FontWeight.W300,
        )

        TextCoins(avatar.price.toString())
    }
}

@Composable
private fun NotEnoughCoinsView(phase: ShopPhase, onDismiss: () -> Unit) {
    DialogMenu(
        title = "Not Enough Coins!",
        isVisible = phase == ShopPhase.NotEnoughCoins,
        options = listOf(
            DialogOption("Okay", onDismiss, false),
        ),
    ) {}
}

@Composable
private fun ConfirmedView(phase: ShopPhase, onDismiss: () -> Unit) {
    DialogMenu(
        title = "Successfully Purchased",
        isVisible = phase == ShopPhase.Confirmed,
        options = listOf(
            DialogOption("Okay", onDismiss, false),
        ),
    ) {}
}

@Composable
fun ShopMenu(onBack: NavFn, onGetCoins: NavFn) {
    val playerRepo = koinInject<PlayerRepository>()
    val playerState = remember {
        PlayerState().apply {
            applySave(playerRepo.load())
        }
    }

    var phase by remember { mutableStateOf<ShopPhase>(ShopPhase.Idle) }

    LaunchedEffect(phase, playerState.avatar) {
        playerRepo.save(playerState.toSave())
    }

    Menu("Shop", onBack = onBack, topContent = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
        ) {
            SpringButton(
                onClick = onGetCoins,
            ) {
                Image(
                    painter = painterResource(Res.drawable.cart),
                    contentDescription = "Cart",
                    modifier = Modifier
                        .size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Image(
                painter = painterResource(Res.drawable.ui_coins),
                contentDescription = "Coins",
                modifier = Modifier
                    .size(18.dp),
            )
            TextDisplay("${playerState.coins}")
        }
    }) {
        ShopMenuView(
            playerState,
            onAvatarClicked = { idx ->
                if(playerState.isUnlockedAvatar(idx))
                    return@ShopMenuView

                phase = ShopPhase.ConfirmingAvatar(idx)
            }
        )
    }

    ConfirmAvatarView(
        phase,
        onDismiss = { phase = ShopPhase.Idle },
        onConfirm = {
            phase = if(playerState.unlockAvatar(it)) ShopPhase.Confirmed else ShopPhase.NotEnoughCoins
        }
    )
    NotEnoughCoinsView(
        phase,
        onDismiss = { phase = ShopPhase.Idle },
    )
    ConfirmedView(
        phase,
        onDismiss = { phase = ShopPhase.Idle }
    )
}

@Composable
@Preview
fun ShopMenuPreview() {
    val playerState = PlayerState()
    playerState.coins = 120
    playerState.unlockAvatar(2)
    playerState.unlockAvatar(5)
    playerState.unlockAvatar(8)

    AppTheme {
        Menu("Shop", onBack = {}, topContent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                Image(
                    painter = painterResource(Res.drawable.ui_coins),
                    contentDescription = "Coins",
                    modifier = Modifier
                        .size(18.dp),
                )
                TextDisplay("${playerState.coins}")
            }
        }) {
            ShopMenuView(playerState, {})
        }

        ConfirmAvatarView(ShopPhase.ConfirmingAvatar(2), {}, {})
    }
}