package com.kldevs.tsunahiki.purchases

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kldevs.tsunahiki.game.PlayerRepository
import com.kldevs.tsunahiki.game.PlayerState
import com.kldevs.tsunahiki.game.utils.SpringButton
import com.kldevs.tsunahiki.menu.Menu
import com.kldevs.tsunahiki.menu.TextDisplay
import com.kldevs.tsunahiki.navigation.NavFn
import com.kldevs.tsunahiki.ui.theme.AppTheme
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import tsunahiki.shared.generated.resources.Res
import tsunahiki.shared.generated.resources.ui_coins

/* ------------------------------------------------------------------ */
/*  Small building blocks                                              */
/* ------------------------------------------------------------------ */

@Composable
private fun CoinIcon(size: Dp = 64.dp) {
    Image(
        painter = painterResource(Res.drawable.ui_coins),
        contentDescription = "Coins",
        modifier = Modifier.size(size),
    )
}

@Composable
private fun PackBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        TextDisplay(
            text,
            fontSize = 9.sp,
            fontWeight = FontWeight.W800,
            color = Color.White,
        )
    }
}

@Composable
private fun CoinsPill(coins: Int) {
    val surfaceBright = MaterialTheme.colorScheme.surfaceBright
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(25.dp))
            .background(surfaceBright)
            .border(2.dp, surfaceVariant, RoundedCornerShape(25.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        CoinIcon(20.dp)
        TextDisplay(coins.toString(), fontSize = 16.sp)
    }
}

/* ------------------------------------------------------------------ */
/*  Cards                                                              */
/* ------------------------------------------------------------------ */

@Composable
private fun CoinPackCard(
    amount: Int,
    price: String,
    modifier: Modifier = Modifier,
    badge: String? = null,
    highlighted: Boolean = false,
    onClick: () -> Unit,
) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val surfaceBright = MaterialTheme.colorScheme.surfaceBright
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    val shape = RoundedCornerShape(16.dp)
    val borderColor = if (highlighted) secondary else surfaceVariant
    val borderWidth = if (highlighted) 3.dp else 2.dp

    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .clip(shape)
                .background(surfaceBright)
                .border(borderWidth, borderColor, shape)
                .padding(horizontal = 12.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CoinIcon(64.dp)
            Spacer(Modifier.height(10.dp))
            TextDisplay(amount.toString(), fontSize = 22.sp)
            Spacer(Modifier.height(12.dp))
            SpringButton(
                onClick = onClick,
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier.fillMaxWidth().height(40.dp),
                containerColor = primary,
            ) {
                TextDisplay(price, fontSize = 15.sp, color = Color.White)
            }
        }

        if (badge != null) {
            PackBadge(
                text = badge,
                color = secondary,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 6.dp),
            )
        }
    }
}

@Composable
private fun StarterPackCard(
    coins: Int,
    price: String,
    onClick: () -> Unit,
) {
    val secondary = MaterialTheme.colorScheme.secondary
    val surfaceBright = MaterialTheme.colorScheme.surfaceBright
    val shape = RoundedCornerShape(16.dp)

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .clip(shape)
                .background(surfaceBright)
                .border(3.dp, secondary, shape)
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                TextDisplay("Starter Pack", fontSize = 20.sp)
                Spacer(Modifier.height(2.dp))
                TextDisplay(
                    "+$coins Coins",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W600,
                    color = secondary,
                )
            }
            SpringButton(
                onClick = onClick,
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier.width(110.dp).height(44.dp),
                containerColor = secondary,
            ) {
                TextDisplay(price, fontSize = 16.sp, color = Color.White)
            }
        }

        PackBadge(
            text = "BEST DEAL",
            color = secondary,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp),
        )
    }
}

/* ------------------------------------------------------------------ */
/*  Screen body                                                        */
/* ------------------------------------------------------------------ */

@Composable
private fun GetCoinsView(
    playerState: PlayerState,
    viewModel: CoinStoreViewModel,
) {
    val surfaceContainerLowest = MaterialTheme.colorScheme.surfaceContainerLowest
    val offering = viewModel.offering

    // Find packages by identifier
    fun pkg(id: String) = offering?.availablePackages?.firstOrNull { it.identifier == id }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // ---- Row 1 : 100 / 500 ----
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            pkg("coins_100")?.let { p ->
                CoinPackCard(
                    amount = 100,
                    price = p.storeProduct.price.formatted,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.purchase(p) { coins ->
                            playerState.coins += coins
                        }
                    },
                )
            }
            pkg("coins_500")?.let { p ->
                CoinPackCard(
                    amount = 500,
                    price = p.storeProduct.price.formatted,
                    badge = "POPULAR",
                    highlighted = true,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.purchase(p) { coins ->
                            playerState.coins += coins
                        }
                    },
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // ---- Row 2 : 1200 / 2500 ----
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            pkg("coins_1200")?.let { p ->
                CoinPackCard(
                    amount = 1200,
                    price = p.storeProduct.price.formatted,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.purchase(p) { coins ->
                            playerState.coins += coins
                        }
                    },
                )
            }
            pkg("coins_2500")?.let { p ->
                CoinPackCard(
                    amount = 2500,
                    price = p.storeProduct.price.formatted,
                    badge = "BEST VALUE",
                    highlighted = true,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.purchase(p) { coins ->
                            playerState.coins += coins
                        }
                    },
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // ---- Starter pack ----
        pkg("starter_pack")?.let { p ->
            StarterPackCard(
                coins = 1000,
                price = p.storeProduct.price.formatted,
                onClick = {
                    viewModel.purchase(p) { coins ->
                        playerState.coins += coins
                    }
                },
            )
        }

        Spacer(Modifier.height(28.dp))

        // ---- Restore purchases ----
        TextDisplay(
            "Restore Purchases",
            fontSize = 13.sp,
            fontWeight = FontWeight.W700,
            color = surfaceContainerLowest,
        )

        Spacer(Modifier.height(24.dp))

        SpringButton(
            onClick = {
                playerState.coins += 1000
            }
        ) {
            TextDisplay("GM")
        }
    }
}

/* ------------------------------------------------------------------ */
/*  Entry point                                                        */
/* ------------------------------------------------------------------ */

@Composable
fun GetCoinsMenu(onBack: NavFn) {
    val playerRepo = koinInject<PlayerRepository>()
    val playerState = remember { PlayerState().apply { applySave(playerRepo.load()) } }
    val viewModel = koinViewModel<CoinStoreViewModel>()

    LaunchedEffect(playerState.coins) {
        playerRepo.save(playerState.toSave())
    }

    Menu(
        title = "Get Coins",
        onBack = onBack,
        topContent = { CoinsPill(playerState.coins) },
    ) {
        GetCoinsView(playerState, viewModel)
    }
}

/* ------------------------------------------------------------------ */
/*  Preview                                                            */
/* ------------------------------------------------------------------ */

@Composable
@Preview
fun GetCoinsPreview() {
    AppTheme {
        val playerState = remember {
            PlayerState().apply {
                coins = 120
                name = "Player"
            }
        }
        // Preview with a mock — the real viewModel will load from RevenueCat
        Menu(
            title = "Get Coins",
            onBack = {},
            topContent = { CoinsPill(playerState.coins) },
        ) {
            // Static preview content
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TextDisplay("Offering preview (loads live data on device)")
            }
        }
    }
}