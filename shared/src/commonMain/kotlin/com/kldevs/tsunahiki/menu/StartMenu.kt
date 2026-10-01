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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kldevs.tsunahiki.audio.AudioEngine
import com.kldevs.tsunahiki.game.utils.SpringButton
import com.kldevs.tsunahiki.navigation.NavFn
import com.kldevs.tsunahiki.ui.theme.AppTheme
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import tsunahiki.shared.generated.resources.Res
import tsunahiki.shared.generated.resources.illustration_start
import tsunahiki.shared.generated.resources.play
import tsunahiki.shared.generated.resources.profile
import tsunahiki.shared.generated.resources.store
import tsunahiki.shared.generated.resources.title

@Composable
fun StartMenu(onStart: NavFn, onShop: NavFn, onProfile: NavFn) {
    val surface = MaterialTheme.colorScheme.surface
    val primary = MaterialTheme.colorScheme.primary

    val audio = koinInject<AudioEngine>()

    LaunchedEffect(Unit) {
        audio.playMusic(Res.getUri("files/sfx/music/calm1.ogg"))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(surface),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        Image(
            painter = painterResource(Res.drawable.title),
            contentDescription = "Title",
            contentScale = ContentScale.FillWidth,
        )
        Text(
            "Lingo Clash - Pull to Win",
            modifier = Modifier
                .padding(top = 8.dp, bottom = 32.dp),
            fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 12.sp,
            fontWeight = FontWeight.W800,
        )
        Image(
            painter = painterResource(Res.drawable.illustration_start),
            contentDescription = "Illustration",
            contentScale = ContentScale.FillWidth,
        )

        SpringButton(
            onClick = { onStart() },
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(bottom = 30.dp)
                .height(55.dp)
                .shadow(10.dp, ambientColor = primary),
            containerColor = primary,
        ) {
            Text(
                "START GAME",
                fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                fontSize = 28.sp,
                fontWeight = FontWeight.W800,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Image(
                painter = painterResource(Res.drawable.play),
                contentDescription = "Play",
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            SpringButton(
                onClick = { onShop() },
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier.fillMaxWidth(0.5f).height(50.dp),
                containerColor = surface,
            ) {
                Text(
                    "Shop",
                    fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W800,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Image(
                    painter = painterResource(Res.drawable.store),
                    contentDescription = "Shop",
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            SpringButton(
                onClick = { onProfile() },
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier.width(50.dp).height(50.dp),
                containerColor = surface,
            ) {
                Image(
                    painter = painterResource(Res.drawable.profile),
                    contentDescription = "Profile",
                )
            }
        }
    }
}

@Composable
@Preview
fun StartMenuPreview() {
    AppTheme {
        StartMenu({}, {}, {})
    }
}